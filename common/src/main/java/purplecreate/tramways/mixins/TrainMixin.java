package purplecreate.tramways.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.trains.entity.Navigation;
import com.simibubi.create.content.trains.graph.DimensionPalette;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.schedule.ScheduleRuntime;
import de.mrjulsen.crn.data.train.portable.TrainDisplayData;
import net.createmod.catnip.data.Pair;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import purplecreate.tramways.TExtras;
import purplecreate.tramways.TNetworking;
import purplecreate.tramways.content.announcements.config.AnnouncementConfig;
import purplecreate.tramways.content.announcements.config.AnnouncementEvent;
import purplecreate.tramways.content.announcements.config.AnnouncementVariant;
import purplecreate.tramways.content.announcements.network.TrainAnnouncementEventS2CPacket;
import purplecreate.tramways.content.signs.TramSignPoint;
import com.simibubi.create.content.trains.entity.Train;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import purplecreate.tramways.content.signs.demands.SignDemand;
import purplecreate.tramways.mixinInterfaces.ITram;

import java.util.*;

@Mixin(value = Train.class, remap = false)
public class TrainMixin implements ITram {
  @Shadow public double throttle;
  @Shadow public TrackGraph graph;
  @Shadow public Navigation navigation;
  @Shadow public ScheduleRuntime runtime;

  @Shadow public UUID id;
  @Unique private Double tramways$storedPermanent;
  @Unique private double tramways$primaryLimit = 1;
  @Unique private Map<Pair<UUID, Boolean>, Double> tramways$signs = new HashMap<>();
  @Unique private AnnouncementConfig tramways$announcementConfig = new AnnouncementConfig(true);
  @Unique private AnnouncementEvent tramways$lastAnnouncementEvent = null;

  @Inject(method = "tick", at = @At(value = "HEAD"))
  private void tramways$tickSpeakers(Level level, CallbackInfo ci) {
    if (level.isClientSide) return;

    AnnouncementConfig config = tramways$announcementConfig;

    double start = navigation.distanceStartedAt;
    double distanceToDestination = navigation.distanceToDestination;

    AnnouncementEvent event = null;
    if (runtime.state == ScheduleRuntime.State.POST_TRANSIT || distanceToDestination == 0) {
      event = AnnouncementEvent.STOPPED_AT;
    } else if (distanceToDestination < Math.min(100, start / 4)) {
      event = AnnouncementEvent.APPROACHING_STATION;
    } else if (start - distanceToDestination > Math.min(30, start / 4)) {
      event = AnnouncementEvent.DEPARTING_STATION;
    }

    if (event == null || tramways$lastAnnouncementEvent == event) return;
    tramways$lastAnnouncementEvent = event;

    AnnouncementVariant variant = config.getVariantFor(event);
    if (variant == null || variant.count() < 1) return;

    TrainDisplayData data = TrainDisplayData.of((Train)(Object)this);
    TNetworking.sendToAll(new TrainAnnouncementEventS2CPacket(id, variant, data));
  }

  @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/entity/Navigation;tick(Lnet/minecraft/world/level/Level;)V", shift = At.Shift.AFTER), remap = true)
  private void tramways$tickSigns(Level level, CallbackInfo ci) {
    Double nextPermanent = null;
    Double nextTemporary = null;
    boolean restorePermanent = false;

    for (var entry : tramways$signs.entrySet()) {
      Pair<UUID, Boolean> pair = entry.getKey();
      double distance = entry.getValue();

      TramSignPoint point = graph.getPoint(TExtras.EdgePointTypes.TRAM_SIGN, pair.getFirst());
      Set<TramSignPoint.SignData> signs = point.getSignData(pair.getSecond());

      for (TramSignPoint.SignData sign : signs) {
        SignDemand.Result result = sign.execute((Train)(Object)this, distance);

        if (result == null) continue;

        if (result.permanent != null) {
          nextPermanent = nextPermanent == null
            ? result.permanent
            : Math.min(result.permanent, nextPermanent);
        }

        if (result.temporary != null) {
          nextTemporary = nextTemporary == null
            ? result.temporary
            : Math.min(result.temporary, nextTemporary);
        }

        if (result.restorePermanent) {
          restorePermanent = true;
        }
      }
    }

    boolean manual = SignDemand.isManual((Train)(Object)this);
    if (nextTemporary != null) {
      if (tramways$storedPermanent == null) {
        tramways$storedPermanent = nextPermanent == null ? throttle : nextPermanent;
      }

      if (!manual) {
        throttle = nextTemporary;
      }
    } else if (restorePermanent) {
      if (tramways$storedPermanent != null && !manual) {
        throttle = tramways$storedPermanent;
      }

      tramways$storedPermanent = null;
    }

    if (nextPermanent != null) {
      if (tramways$storedPermanent == null) {
        throttle = nextPermanent;
      } else {
        tramways$storedPermanent = nextPermanent;
      }
    }
  }

//  @Inject(method = "frontSignalListener", at = @At("RETURN"), cancellable = true)
//  private void tramways$approachTramSign(CallbackInfoReturnable<TravellingPoint.IEdgePointListener> cir) {
//    TravellingPoint.IEdgePointListener originalListener = cir.getReturnValue();
//    cir.setReturnValue((distance, couple) -> {
//      if (couple.getFirst() instanceof TramSignPoint sign) {
//        TrackNode node = couple.getSecond().getSecond();
//        tramways$putSign(sign.id, sign.isPrimary(node), 0.0);
//        return false;
//      }
//
//      return originalListener.test(distance, couple);
//    });
//  }

//  @Inject(method = "backSignalListener", at = @At("RETURN"), cancellable = true)
//  private void tramways$passedTramSign(CallbackInfoReturnable<TravellingPoint.IEdgePointListener> cir) {
//    TravellingPoint.IEdgePointListener originalListener = cir.getReturnValue();
//    cir.setReturnValue((distance, couple) -> {
//      if (couple.getFirst() instanceof TramSignPoint sign) {
//        TrackNode node = couple.getSecond().getSecond();
//        tramways$signs.remove(Pair.of(sign.id, sign.isPrimary(node)));
//        return false;
//      }
//
//      return originalListener.test(distance, couple);
//    });
//  }

  @Inject(method = "read", at = @At("RETURN"))
  private static void tramways$readMixin(
    CompoundTag tag,
    Map<UUID, TrackGraph> trackNetworks,
    DimensionPalette dimensions,
    CallbackInfoReturnable<Train> cir,
    @Local Train train
  ) {
    if (train instanceof ITram tram) {
      tram.tramways$read(tag);
    }
  }

  @Unique
  @Override
  public void tramways$read(CompoundTag tag) {
    if (tag.contains("Tramways$StoredPermanent")) {
      tramways$storedPermanent = tag.getDouble("Tramways$StoredPermanent");
    }

    if (tag.contains("Tramways$PrimaryLimit")) {
      tramways$primaryLimit = tag.getDouble("Tramways$PrimaryLimit");
    }

    if (tag.contains("Tramways$Signs")) {
      NBTHelper.iterateCompoundList(tag.getList("Tramways$Signs", 10), (t) -> {
        tramways$putSign(t.getUUID("Id"), t.getBoolean("Primary"), t.getDouble("Distance"));
      });
    }

    if (tag.contains("Tramways$AnnouncementConfig")) {
      tramways$announcementConfig = AnnouncementConfig.fromNbt(tag.getCompound("Tramways$AnnouncementConfig"), true);
    }
  }

  @Inject(method = "write", at = @At("RETURN"))
  private void tramways$write(
    DimensionPalette dimensions,
    CallbackInfoReturnable<CompoundTag> cir,
    @Local CompoundTag tag
  ) {
    if (tramways$storedPermanent != null) {
      tag.putDouble("Tramways$StoredPermanent", tramways$storedPermanent);
    }

    tag.putDouble("Tramways$PrimaryLimit", tramways$primaryLimit);
    tag.put("Tramways$Signs", NBTHelper.writeCompoundList(tramways$signs.entrySet(), (e) -> {
      CompoundTag t = new CompoundTag();

      Pair<UUID, Boolean> pair = e.getKey();
      double distance = e.getValue();

      t.putUUID("Id", pair.getFirst());
      t.putBoolean("Primary", pair.getSecond());
      t.putDouble("Distance", distance);

      return t;
    }));
    tag.put("Tramways$AnnouncementConfig", tramways$announcementConfig.toNbt());
  }

  @Unique
  @Override
  public boolean tramways$hasTemporaryLimit() {
    return tramways$storedPermanent != null;
  }

  @Unique
  @Override
  public void tramways$setPrimaryLimit(double throttle) {
    this.tramways$primaryLimit = throttle;
  }

  @Unique
  @Override
  public double tramways$getPrimaryLimit() {
    return this.tramways$primaryLimit;
  }

  @Unique
  @Override
  public void tramways$putSign(UUID id, boolean primary, double distance) {
    tramways$signs.put(Pair.of(id, primary), distance);
  }

  @Unique
  @Override
  public void tramways$clearSigns() {
    tramways$signs.clear();
  }

  @Unique
  @Override
  public AnnouncementConfig tramways$getAnnouncementConfig() {
    return tramways$announcementConfig;
  }

  @Unique
  @Override
  public void tramways$setAnnouncementConfig(AnnouncementConfig config) {
    tramways$announcementConfig = config;
  }
}
