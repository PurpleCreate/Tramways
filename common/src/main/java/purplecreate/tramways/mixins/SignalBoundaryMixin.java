package purplecreate.tramways.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.Create;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.graph.*;
import com.simibubi.create.content.trains.signal.SignalBlock;
import com.simibubi.create.content.trains.signal.SignalBoundary;
import com.simibubi.create.content.trains.signal.SignalEdgeGroup;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.data.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import purplecreate.tramways.Tramways;
import purplecreate.tramways.content.signals.base.ExtendedSignalState;
import purplecreate.tramways.content.signals.base.JunctionState;
import purplecreate.tramways.mixinInterfaces.IRoutedSignal;

import java.util.*;
import java.util.function.Function;

@Mixin(value = SignalBoundary.class, remap = false)
public abstract class SignalBoundaryMixin implements IRoutedSignal.Internal {
  @Shadow public Couple<Map<BlockPos, Boolean>> blockEntities;
  @Shadow public Couple<SignalBlock.SignalType> types;
  @Shadow public Couple<UUID> groups;
  @Shadow public Couple<Boolean> sidesToUpdate;
  @Shadow private Couple<Map<UUID, Boolean>> chainedSignals;

  @Shadow public abstract boolean isForcedRed(boolean primary);

  @Unique private final Couple<JunctionState> tramways$route = Couple.create(() -> null);
  @Unique private final Couple<UUID> tramways$routeSelectedBy = Couple.create(() -> null);
  @Unique private final Couple<List<Pair<SignalBoundary, Boolean>>> tramways$selectedRoute = Couple.create(() -> null);
  @Unique private final Couple<Map<Pair<SignalBoundary, Boolean>, JunctionState>> tramways$possibleRoutes = Couple.create(HashMap::new);
  @Unique private final Couple<List<Pair<SignalBoundary, Boolean>>> tramways$nextSignals = Couple.create(() -> null);

  @Override
  public <T> T tramways$getSide(BlockPos pos, T defaultValue, Function<Boolean, T> func) {
    for (boolean front : Iterate.trueAndFalse) {
      if (blockEntities.get(front).containsKey(pos)) {
        return func.apply(front);
      }
    }

    return defaultValue;
  }

  @Override
  public List<Pair<SignalBoundary, Boolean>> tramways$getNextSignal(boolean front) {
    if (types.get(front) == SignalBlock.SignalType.CROSS_SIGNAL) {
      List<Pair<SignalBoundary, Boolean>> route = tramways$selectedRoute.get(front);
      if (route == null) return null;
      Pair<SignalBoundary, Boolean> exit = route.get(route.size() - 1);
      if (!(exit.getFirst() instanceof IRoutedSignal routedSignal)) return null;
      return routedSignal.tramways$getNextSignal(exit.getSecond());
    } else {
      return tramways$nextSignals.get(front);
    }
  }

  @Unique
  private List<Pair<SignalBoundary, Boolean>> tramways$getNextSignal(TrackGraph graph, boolean front) {
    if (types.get(front) == SignalBlock.SignalType.CROSS_SIGNAL) return null;

    List<Pair<SignalBoundary, Boolean>> signals = new ArrayList<>();

    SignalBoundary self = (SignalBoundary)(Object)this;
    Couple<TrackNode> startNodes = self.edgeLocation.map(graph::locateNode);
    Couple<TrackEdge> startEdges = startNodes.mapWithParams(
      (l1, l2) -> graph.getConnectionsFrom(l1).get(l2),
      startNodes.swap()
    );

    TrackNode node1 = startNodes.get(front);
    TrackNode node2 = startNodes.get(!front);
    TrackEdge startEdge = startEdges.get(front);

    if (startEdge == null) return signals;

    SignalBoundary nextSignal = startEdge.getEdgeData().next(EdgePointType.SIGNAL, self.getLocationOn(startEdge));
    if (nextSignal != null) {
      signals.add(Pair.of(nextSignal, !nextSignal.isPrimary(node1)));
      return signals;
    }

    Set<TrackEdge> visited = new HashSet<>();
    List<Couple<TrackNode>> frontier = new ArrayList<>();
    frontier.add(Couple.create(node2, node1));

    while (!frontier.isEmpty()) {
      Couple<TrackNode> couple = frontier.remove(0);
      TrackNode currentNode = couple.getFirst();
      TrackNode prevNode = couple.getSecond();

      edgeWalk:
      for (Map.Entry<TrackNode, TrackEdge> entry : graph.getConnectionsFrom(currentNode).entrySet()) {
        TrackNode nextNode = entry.getKey();
        TrackEdge edge = entry.getValue();

        if (nextNode == prevNode) continue;
        if (!visited.add(edge)) continue;

        TrackEdge oppositeEdge = graph.getConnectionsFrom(nextNode).get(currentNode);
        visited.add(oppositeEdge);

        for (boolean flip : Iterate.falseAndTrue) {
          TrackEdge currentEdge = flip ? oppositeEdge : edge;
          EdgeData signalData = currentEdge.getEdgeData();
          if (!signalData.hasSignalBoundaries()) continue;

          SignalBoundary nextBoundary = signalData.next(EdgePointType.SIGNAL, 0);
          if (nextBoundary == null) continue;

          signals.add(Pair.of(nextBoundary, !nextBoundary.isPrimary(currentNode)));
          continue edgeWalk;
        }

        frontier.add(Couple.create(nextNode, currentNode));
      }
    }

    return signals;
  }

  @Override
  public ExtendedSignalState tramways$getOccupied(boolean front) {
    UUID groupId = groups.get(front);
    SignalEdgeGroup group = Create.RAILWAYS.signalEdgeGroups.get(groupId);
    if (groupId == groups.get(!front) || group == null) return ExtendedSignalState.INVALID;

    if (isForcedRed(front)) {
      return ExtendedSignalState.DANGER;
    } else {
      for (SignalEdgeGroup current : group.intersectingResolved) {
        for (Train train : current.trains) {
          if (train.occupiedSignalBlocks.containsKey(current.id)) {
            return ExtendedSignalState.DANGER;
          }
        }
      }
    }

    return ExtendedSignalState.CLEAR;
  }

  @Override
  public ExtendedSignalState tramways$getExtendedState(boolean front) {
    return tramways$getExtendedState(front, 0);
  }

  @Override
  public ExtendedSignalState tramways$getExtendedState(boolean front, int depth) {
    if (depth > 2) return ExtendedSignalState.INVALID;

    ExtendedSignalState occupied = tramways$getOccupied(front);

    if (occupied != ExtendedSignalState.CLEAR) {
      return occupied;
    } else if (tramways$getPossibleRoutes(front).isEmpty()) {
      List<Pair<SignalBoundary, Boolean>> nextSignals = tramways$getNextSignal(front);
      if (nextSignals == null) return ExtendedSignalState.CLEAR;

      boolean preCaution = false;
      boolean caution = false;

      for (Pair<SignalBoundary, Boolean> pair : nextSignals) {
        if (!(pair.getFirst() instanceof IRoutedSignal.Internal routedSignal)) continue;

        switch (routedSignal.tramways$getExtendedState(pair.getSecond(), depth + 1)) {
          case DANGER, NO_ROUTE_SET -> caution = true;
          case CAUTION -> preCaution = true;
        }
      }

      if (caution) {
        return ExtendedSignalState.CAUTION;
      } else if (preCaution) {
        return ExtendedSignalState.PRE_CAUTION;
      } else {
        return ExtendedSignalState.CLEAR;
      }
    } else {
      List<Pair<SignalBoundary, Boolean>> route = tramways$selectedRoute.get(front);
      if (route == null) return ExtendedSignalState.NO_ROUTE_SET;

      for (int i = 0; i < route.size() - 1; i++) {
        Pair<SignalBoundary, Boolean> pair = route.get(i);

        switch (pair.getFirst().cachedStates.get(pair.getSecond())) {
          case INVALID -> {
            return ExtendedSignalState.INVALID;
          }
          case RED -> {
            return ExtendedSignalState.DANGER;
          }
        }
      }

      Pair<SignalBoundary, Boolean> exit = route.get(route.size() - 1);
      if (!(exit.getFirst() instanceof IRoutedSignal.Internal signal)) return ExtendedSignalState.INVALID;
      return signal.tramways$getExtendedState(exit.getSecond(), depth);
    }
  }

  @Override
  public void tramways$notifySelectedRoute(boolean front, Train train, List<Pair<SignalBoundary, Boolean>> path) {
    if (tramways$routeSelectedBy.get(front) != null) return;
    tramways$routeSelectedBy.set(front, train.id);
    tramways$selectedRoute.set(front, path);
  }

  @Override
  public void tramways$unnotifySelectedRoute(boolean front, Train train) {
    if (tramways$routeSelectedBy.get(front) != train.id) return;
    tramways$routeSelectedBy.set(front, null);
    tramways$selectedRoute.set(front, null);
  }

  @Override
  public @Nullable JunctionState tramways$getSelectedRoute(boolean front) {
    List<Pair<SignalBoundary, Boolean>> route = tramways$selectedRoute.get(front);
    if (route == null) return null;
    Pair<SignalBoundary, Boolean> exit = route.get(route.size() - 1);
    return tramways$possibleRoutes.get(front).get(exit);
  }

  @Override
  public JunctionState tramways$getRoute(boolean front) {
    if (types.get(front) == SignalBlock.SignalType.ENTRY_SIGNAL) {
      return tramways$route.get(front);
    }

    return null;
  }

  @Override
  public List<JunctionState.SignalInfo> tramways$getPossibleRoutes(boolean front) {
    return tramways$possibleRoutes.get(front)
      .entrySet()
      .stream()
      .map(entry ->
        new JunctionState.SignalInfo(entry.getKey().getFirst(), entry.getKey().getSecond(), entry.getValue())
      )
      .toList();
  }

  @Override
  public Map<Pair<SignalBoundary, Boolean>, JunctionState> tramways$getPossibleRoutes(TrackGraph graph, boolean front, int ttl) {
    Map<Pair<SignalBoundary, Boolean>, JunctionState> routes = new HashMap<>();
    Map<UUID, Boolean> chain = chainedSignals.get(front);

    if (ttl <= 0) return routes;
    if (types.get(front) != SignalBlock.SignalType.CROSS_SIGNAL || chain == null) return routes;

    for (Map.Entry<UUID, Boolean> entry : chain.entrySet()) {
      UUID id = entry.getKey();
      boolean otherSide = entry.getValue();
      SignalBoundary otherSignal = graph.getPoint(EdgePointType.SIGNAL, id);
      if (!(otherSignal instanceof IRoutedSignal.Internal otherRouted)) continue;

      if (otherSignal.types.get(otherSide) == SignalBlock.SignalType.CROSS_SIGNAL) {
        routes.putAll(otherRouted.tramways$getPossibleRoutes(graph, otherSide, ttl - 1));
      } else {
        JunctionState route = otherRouted.tramways$getRoute(otherSide);
        if (route != null) routes.put(Pair.of(otherSignal, otherSide), route);
      }
    }

    return routes;
  }

  @Override
  public void tramways$assignRoute(boolean front, JunctionState route) {
    tramways$route.set(front, route);
  }

  @Inject(method = "tick", at = @At("HEAD"))
  private void tramways$cacheRouting(TrackGraph graph, boolean preTrains, CallbackInfo ci) {
    if (!preTrains) return;

    for (boolean front : Iterate.trueAndFalse) {
      tramways$possibleRoutes.set(front, tramways$getPossibleRoutes(graph, front, 50));

      if (tramways$nextSignals.get(front) == null || sidesToUpdate.get(front))
        tramways$nextSignals.set(front, tramways$getNextSignal(graph, front));
    }
  }

  @Inject(method = "setGroupAndUpdate", at = @At("TAIL"))
  private void tramways$removeNextSignalCache(TrackNode side, UUID groupId, CallbackInfo ci, @Local boolean front) {
    tramways$nextSignals.set(front, null);
  }

  @Inject(method = "read(Lnet/minecraft/nbt/CompoundTag;ZLcom/simibubi/create/content/trains/graph/DimensionPalette;)V", at = @At("HEAD"))
  private void tramways$read(CompoundTag nbt, boolean migration, DimensionPalette dimensions, CallbackInfo ci) {
    for (int i = 1; i <= 2; i++)
      tramways$route.set(i == 1, JunctionState.fromNbt(nbt.getCompound("Tramways$Route" + i)));
  }

  @Inject(method = "write(Lnet/minecraft/nbt/CompoundTag;Lcom/simibubi/create/content/trains/graph/DimensionPalette;)V", at = @At("HEAD"))
  private void tramways$write(CompoundTag nbt, DimensionPalette dimensions, CallbackInfo ci) {
    for (int i = 1; i <= 2; i++) {
      JunctionState route = tramways$route.get(i == 1);
      if (route == null) continue;
      nbt.put("Tramways$Route" + i, route.toNbt());
    }
  }
}
