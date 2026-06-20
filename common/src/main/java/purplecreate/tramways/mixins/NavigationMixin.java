package purplecreate.tramways.mixins;

import com.simibubi.create.content.trains.entity.TravellingPoint;
import com.simibubi.create.content.trains.graph.DiscoveredPath;
import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.schedule.Schedule;
import com.simibubi.create.content.trains.schedule.ScheduleEntry;
import com.simibubi.create.content.trains.schedule.ScheduleRuntime;
import com.simibubi.create.content.trains.signal.SignalBlock;
import com.simibubi.create.content.trains.signal.SignalBoundary;
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
import purplecreate.tramways.mixinInterfaces.IRoutedSignal;
import purplecreate.tramways.mixinInterfaces.ITram;
import purplecreate.tramways.mixinInterfaces.ITramNavigation;

import java.util.*;

@Mixin(value = Navigation.class, remap = false)
public abstract class NavigationMixin implements ITramNavigation {
  @Unique private final List<Pair<SignalBoundary, Boolean>> tramways$chainSignals = new ArrayList<>();
  @Unique private final List<Pair<IRoutedSignal, Boolean>> tramways$notifiedSignals = new ArrayList<>();
  @Unique private boolean tramways$routeCancelled;
  @Unique private DiscoveredPath tramways$junctionRoute;
  @Unique private Runnable tramways$junctionRouteCompleted;

  @Shadow public Train train;
  @Shadow public double distanceToDestination;
  @Shadow public double distanceStartedAt;
  @Shadow private List<Couple<TrackNode>> currentPath;
  @Shadow public GlobalStation destination;
  @Shadow protected abstract Map.Entry<TrackNode, TrackEdge> navigateOptions(List<Couple<TrackNode>> path, TrackGraph graph, List<Map.Entry<TrackNode, TrackEdge>> options);

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

    // ↓ route selection ↓

    if (couple.getFirst() instanceof SignalBoundary signal) {
      UUID entering = signal.getGroup(couple.getSecond().getSecond());
      boolean front = entering.equals(signal.groups.getFirst());

      if (signal.types.get(front) == SignalBlock.SignalType.CROSS_SIGNAL) {
        tramways$chainSignals.add(Pair.of(signal, front));
      } else if (!tramways$chainSignals.isEmpty()) {
        tramways$chainSignals.forEach(pair -> {
          if (!(pair.getFirst() instanceof IRoutedSignal routedSignal)) return;

          routedSignal.tramways$notifySelectedRoute(pair.getSecond(), train, Pair.of(signal, front));
          tramways$notifiedSignals.add(Pair.of(routedSignal, pair.getSecond()));
        });
        tramways$chainSignals.clear();
      }
    }
  }

  @Inject(method = "tick", at = @At("HEAD"))
  private void tramways$tickRequestStop(Level level, CallbackInfo ci) {
    if ((train instanceof ITram tram)) {
      tram.tramways$clearSigns();
    }
    tramways$chainSignals.clear();

    tramways$notifiedSignals.forEach(pair ->
      pair.getFirst().tramways$unnotifySelectedRoute(pair.getSecond(), train)
    );
    tramways$notifiedSignals.clear();

    // ↑     other rubbish       ↑
    // ↓ request stop tick logic ↓

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

  // junction control

  @Unique
  @Override
  public void tramways$setRouteThroughJunction(DiscoveredPath path, Runnable onComplete) {
    tramways$junctionRoute = path;
    tramways$junctionRouteCompleted = onComplete;
  }

  @Override
  public void tramways$cancelRouteThroughJunction() {
    tramways$junctionRoute = null;
    tramways$junctionRouteCompleted = null;
  }

  @Inject(method = "control", at = @At("HEAD"), cancellable = true)
  public void tramways$junctionControl(TravellingPoint mp, CallbackInfoReturnable<TravellingPoint.ITrackSelector> cir) {
    if (tramways$junctionRoute == null) return;

    if (tramways$junctionRoute.path.isEmpty()) {
      if (tramways$junctionRouteCompleted != null) {
        tramways$junctionRouteCompleted.run();
      }

      tramways$junctionRoute = null;
      tramways$junctionRouteCompleted = null;
    } else if (destination == null) {
      cir.setReturnValue((graph, pair) ->
        navigateOptions(tramways$junctionRoute.path, graph, pair.getSecond())
      );
    }
  }
}
