package purplecreate.tramways.mixins;

import com.simibubi.create.content.trains.schedule.Schedule;
import com.simibubi.create.content.trains.schedule.ScheduleEntry;
import com.simibubi.create.content.trains.schedule.ScheduleRuntime;
import com.simibubi.create.content.trains.station.GlobalStation;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import purplecreate.tramways.content.requestStop.train.RequestStopInstruction;
import purplecreate.tramways.content.requestStop.RequestStopServer;
import purplecreate.tramways.content.signs.TramSignPoint;
import com.simibubi.create.content.trains.entity.Navigation;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.graph.TrackNode;
import com.simibubi.create.content.trains.signal.TrackEdgePoint;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.Pair;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import purplecreate.tramways.mixinInterfaces.ITram;
import purplecreate.tramways.mixinInterfaces.IStopRequestableNavigation;

import java.util.List;
import java.util.UUID;

@Mixin(value = Navigation.class, remap = false)
public abstract class NavigationMixin implements IStopRequestableNavigation {
  @Unique private boolean tramways$routeCancelled;
  @Shadow public Train train;
  @Shadow public double distanceToDestination;
  @Shadow public double distanceStartedAt;
  @Shadow private List<Couple<TrackNode>> currentPath;
  @Shadow public GlobalStation destination;

  @Unique
  private void tramways$cancelRoute() {
    currentPath.clear();
    if (destination != null) {
      destination.cancelReservation(train);
      destination = null;
    }

    train.runtime.state = ScheduleRuntime.State.PRE_TRANSIT;
    train.runtime.currentEntry++;
  }

  @Unique
  public void tramways$resetRouteCancelled() {
    tramways$routeCancelled = false;
  }

  @Inject(method = "lambda$tick$0", at = @At("HEAD"), cancellable = true)
  private void tramways$tickSign(MutableObject<Pair<UUID, Boolean>> trackingCrossSignal,
                                     double scanDistance,
                                     MutableDouble crossSignalDistanceTracker,
                                     double brakingDistanceNoFlicker,
                                     Double distance,
                                     Pair<TrackEdgePoint, Couple<TrackNode>> couple,
                                     CallbackInfoReturnable<Boolean> cir) {
    if (couple.getFirst() instanceof TramSignPoint sign) {
      if (train instanceof ITram tram) {
        TrackNode node = couple.getSecond().getSecond();
        tram.tramways$putSign(sign.id, sign.isPrimary(node), distance);
        cir.setReturnValue(false);
      }
    }
  }

  @Inject(method = "tick", at = @At("HEAD"))
  private void tramways$tickRequestStop(Level level, CallbackInfo ci) {
    Schedule schedule = train.runtime.getSchedule();

    if (
      train.runtime.paused
        || schedule == null
        || train.runtime.currentEntry >= schedule.entries.size()
    )
      return; // not trying to cancel here

    double acceleration = train.acceleration();
    double brakingDistance = (train.speed * train.speed) / (2 * acceleration);

    ScheduleEntry currentEntry = schedule.entries.get(train.runtime.currentEntry);

    if (
      !(currentEntry.instruction instanceof RequestStopInstruction)
        || distanceToDestination < 1
    )
      return;

    if (distanceToDestination <= brakingDistance) {
      if (!RequestStopServer.shouldStop(train) && !tramways$routeCancelled) {
        tramways$cancelRoute();
        tramways$routeCancelled = true;
      }

      RequestStopServer.removeCountdown(train);
    } else if (!RequestStopServer.shouldStop(train)) {
      RequestStopServer.updateCountdown(
        train,
        (float) ((distanceToDestination - brakingDistance)
          / (distanceStartedAt - brakingDistance))
      );
    }
  }
}
