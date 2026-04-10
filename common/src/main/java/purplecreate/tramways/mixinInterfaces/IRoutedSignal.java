package purplecreate.tramways.mixinInterfaces;

import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.signal.SignalBoundary;
import net.createmod.catnip.data.Pair;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;
import purplecreate.tramways.content.signals.base.ExtendedSignalState;
import purplecreate.tramways.content.signals.base.JunctionState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public interface IRoutedSignal {
  List<Pair<SignalBoundary, Boolean>> tramways$getNextSignal(boolean front);
  default List<Pair<SignalBoundary, Boolean>> tramways$getNextSignal(BlockPos pos) {
    return ((Internal)this).tramways$getSide(pos, new ArrayList<>(), this::tramways$getNextSignal);
  }

  ExtendedSignalState tramways$getExtendedState(boolean front);
  default ExtendedSignalState tramways$getExtendedState(BlockPos pos) {
    return ((Internal)this).tramways$getSide(pos, ExtendedSignalState.INVALID, this::tramways$getExtendedState);
  }

  void tramways$notifySelectedRoute(boolean front, Train train, Pair<SignalBoundary, Boolean> exit);
  void tramways$unnotifySelectedRoute(boolean front, Train train);

  @Nullable JunctionState tramways$getSelectedRoute(boolean front);
  default @Nullable JunctionState tramways$getSelectedRoute(BlockPos pos) {
    return ((Internal)this).tramways$getSide(pos, null, this::tramways$getSelectedRoute);
  }

  @Nullable JunctionState tramways$getRoute(boolean front);
  default @Nullable JunctionState tramways$getRoute(BlockPos pos) {
    return ((Internal)this).tramways$getSide(pos, null, this::tramways$getRoute);
  }

  List<JunctionState> tramways$getPossibleRoutes(boolean front);
  default List<JunctionState> tramways$getPossibleRoutes(BlockPos pos) {
    return ((Internal)this).tramways$getSide(pos, new ArrayList<>(), this::tramways$getPossibleRoutes);
  }

  void tramways$assignRoute(boolean front, @Nullable JunctionState route);
  default void tramways$assignRoute(BlockPos pos, @Nullable JunctionState route) {
    ((Internal)this).tramways$getSide(pos, null, front -> {
      tramways$assignRoute(front, route);
      return null;
    });
  }

  interface Internal extends IRoutedSignal {
    <T> T tramways$getSide(BlockPos pos, T defaultValue, Function<Boolean, T> func);
    Map<UUID, JunctionState> tramways$getPossibleRoutes(TrackGraph graph, boolean front);
    ExtendedSignalState tramways$getExtendedState(boolean front, int depth);
  }
}
