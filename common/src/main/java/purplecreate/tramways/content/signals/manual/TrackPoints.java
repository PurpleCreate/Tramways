package purplecreate.tramways.content.signals.manual;

import com.simibubi.create.Create;
import com.simibubi.create.content.trains.bogey.AbstractBogeyBlock;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.entity.TravellingPoint;
import com.simibubi.create.content.trains.graph.*;
import com.simibubi.create.content.trains.signal.SignalBoundary;
import com.simibubi.create.content.trains.signal.SignalEdgeGroup;
import com.simibubi.create.content.trains.signal.TrackEdgePoint;
import com.simibubi.create.content.trains.station.GlobalStation;
import com.simibubi.create.content.trains.track.TrackMaterial;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.data.Pair;
import net.minecraft.util.Mth;
import purplecreate.tramways.mixinInterfaces.IRoutedSignal;
import purplecreate.tramways.mixins.CarriageBogeyAccessor;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class TrackPoints {
  public Pair<SignalBoundary, Boolean> findNearestJunction(Train train, boolean forward) {
    AtomicReference<Pair<SignalBoundary, Boolean>> result = new AtomicReference<>(null);

    double acceleration = train.acceleration();
    double minDistance = .75f * (train.speed * train.speed) / (2 * acceleration);
    double maxDistance = Math.max(32, 1.5f * (train.speed * train.speed) / (2 * acceleration));

    search(train, maxDistance, forward, (distance, cost, reachedVia, current, edgePoint) -> {
      if (distance < minDistance)
        return false;

      TrackEdge edge = current.getSecond();
      double position = edge.getLength() - edgePoint.getLocationOn(edge);
      if (distance - position < minDistance)
        return false;

      if (!(edgePoint instanceof SignalBoundary boundary)) return false;
      if (!(edgePoint instanceof IRoutedSignal routed)) return false;

      boolean signalForward = boundary.isPrimary(current.getFirst().getSecond());

      if (routed.tramways$getPossibleRoutes(signalForward).isEmpty()) return false;

      result.set(Pair.of(boundary, signalForward));
      return true;
    });

    return result.get();
  }

  public void search(Train train, double maxDistance, boolean forward, PointTest pointTest) {
    search(train, maxDistance, -1, forward, pointTest);
  }

  public void search(Train train, double maxDistance, double maxCost, boolean forward, PointTest pointTest) {
    TrackGraph graph = train.graph;
    if (graph == null)
      return;

    Set<TrackMaterial.TrackType> validTypes = new HashSet<>();
    for (int i = 0; i < train.carriages.size(); i++) {
      Carriage carriage = train.carriages.get(i);

      AbstractBogeyBlock<?> leadingBlock = ((CarriageBogeyAccessor)carriage.leadingBogey()).tramways$getType();
      AbstractBogeyBlock<?> trailingBlock = ((CarriageBogeyAccessor)carriage.trailingBogey()).tramways$getType();

      if (i == 0) {
        validTypes.addAll(leadingBlock.getValidPathfindingTypes(carriage.leadingBogey().getStyle()));
      } else {
        validTypes.retainAll(leadingBlock.getValidPathfindingTypes(carriage.leadingBogey().getStyle()));
      }
      if (carriage.isOnTwoBogeys())
        validTypes.retainAll(trailingBlock.getValidPathfindingTypes(carriage.trailingBogey().getStyle()));
    }
    if (validTypes.isEmpty()) return;

    Map<TrackEdge, Integer> penalties = new IdentityHashMap<>();
    boolean costRelevant = maxCost >= 0;
    if (costRelevant) {
      for (Train otherTrain : Create.RAILWAYS.trains.values()) {
        if (otherTrain.graph != graph)
          continue;
        if (otherTrain == train)
          continue;
        int navigationPenalty = otherTrain.getNavigationPenalty();
        otherTrain.getEndpointEdges()
          .forEach(nodes -> {
            if (nodes.either(Objects::isNull))
              return;
            for (boolean flip : Iterate.trueAndFalse) {
              TrackEdge e = graph.getConnection(flip ? nodes.swap() : nodes);
              if (e == null)
                continue;
              int existing = penalties.getOrDefault(e, 0);
              penalties.put(e, existing + navigationPenalty / 2);
            }
          });
      }
    }

    TravellingPoint startingPoint = forward ? train.carriages.get(0)
      .getLeadingPoint()
      : train.carriages.get(train.carriages.size() - 1)
      .getTrailingPoint();

    Set<TrackEdge> visited = new HashSet<>();
    Map<TrackEdge, Pair<Boolean, Couple<TrackNode>>> reachedVia = new IdentityHashMap<>();
    PriorityQueue<FrontierEntry> frontier = new PriorityQueue<>();

    TrackNode initialNode1 = forward ? startingPoint.node1 : startingPoint.node2;
    TrackNode initialNode2 = forward ? startingPoint.node2 : startingPoint.node1;
    TrackEdge initialEdge = graph.getConnectionsFrom(initialNode1)
      .get(initialNode2);
    if (initialEdge == null)
      return;

    double distanceToNode2 = forward ? initialEdge.getLength() - startingPoint.position : startingPoint.position;

    frontier.add(new FrontierEntry(distanceToNode2, 0, initialNode1, initialNode2, initialEdge));
    int signalWeight = Mth.clamp(train.navigation.ticksWaitingForSignal * 2, Penalties.RED_SIGNAL, 200);

    Search:
    while (!frontier.isEmpty()) {
      FrontierEntry entry = frontier.poll();
      if (!visited.add(entry.edge))
        continue;

      double distance = entry.distance;
      int penalty = entry.penalty;

      if (distance > maxDistance)
        continue;

      TrackEdge edge = entry.edge;
      TrackNode node1 = entry.node1;
      TrackNode node2 = entry.node2;

      if (costRelevant)
        penalty += penalties.getOrDefault(edge, 0);

      EdgeData signalData = edge.getEdgeData();
      if (signalData.hasPoints()) {
        for (TrackEdgePoint point : signalData.getPoints()) {
          if (node1 == initialNode1 && point.getLocationOn(edge) < edge.getLength() - distanceToNode2)
            continue;
          if (costRelevant && distance + penalty > maxCost)
            continue Search;
          if (!point.canNavigateVia(node2))
            continue Search;
          if (point instanceof SignalBoundary signal) {
            if (signal.isForcedRed(node2)) {
              penalty += Penalties.REDSTONE_RED_SIGNAL;
              continue;
            }
            UUID group = signal.getGroup(node2);
            if (group == null)
              continue;
            SignalEdgeGroup signalEdgeGroup = Create.RAILWAYS.signalEdgeGroups.get(group);
            if (signalEdgeGroup == null)
              continue;
            if (signalEdgeGroup.isOccupiedUnless(signal)) {
              penalty += signalWeight;
              signalWeight /= 2;
            }
          }
          if (point instanceof GlobalStation station) {
            Train presentTrain = station.getPresentTrain();
            boolean isOwnStation = presentTrain == train;
            if (presentTrain != null && !isOwnStation)
              penalty += Penalties.STATION_WITH_TRAIN;
            if (station.canApproachFrom(node2) && pointTest.test(distance, distance + penalty, reachedVia,
              Pair.of(Couple.create(node1, node2), edge), station))
              return;
            if (!isOwnStation)
              penalty += Penalties.STATION;
          }
          if (pointTest.test(distance, distance + penalty, reachedVia,
            Pair.of(Couple.create(node1, node2), edge), point))
            return;
        }
      }

      if (costRelevant && distance + penalty > maxCost)
        continue;

      List<Map.Entry<TrackNode, TrackEdge>> validTargets = new ArrayList<>();
      Map<TrackNode, TrackEdge> connectionsFrom = graph.getConnectionsFrom(node2);
      for (Map.Entry<TrackNode, TrackEdge> connection : connectionsFrom.entrySet()) {
        TrackNode newNode = connection.getKey();
        if (newNode == node1)
          continue;
        if (edge.canTravelTo(connection.getValue()))
          validTargets.add(connection);
      }

      if (validTargets.isEmpty())
        continue;

      for (Map.Entry<TrackNode, TrackEdge> target : validTargets) {
        if (!validTypes.contains(target.getValue().getTrackMaterial().trackType))
          continue;
        TrackNode newNode = target.getKey();
        TrackEdge newEdge = target.getValue();
        double newDistance = newEdge.getLength() + distance;
        reachedVia.putIfAbsent(newEdge, Pair.of(validTargets.size() > 1, Couple.create(node1, node2)));
        frontier.add(new FrontierEntry(newDistance, penalty, node2, newNode, newEdge));
      }
    }
  }

  @FunctionalInterface
  public interface PointTest {
    boolean test(
      double distance, double cost, Map<TrackEdge, Pair<Boolean, Couple<TrackNode>>> reachedVia,
      Pair<Couple<TrackNode>, TrackEdge> current, TrackEdgePoint edgePoint
    );
  }

  private static class FrontierEntry implements Comparable<FrontierEntry> {
    double distance;
    int penalty;
    double remaining;
    boolean hasDestination;
    TrackNode node1;
    TrackNode node2;
    TrackEdge edge;

    public FrontierEntry(double distance, int penalty, TrackNode node1, TrackNode node2, TrackEdge edge) {
      this.distance = distance;
      this.penalty = penalty;
      this.remaining = 0;
      this.hasDestination = false;
      this.node1 = node1;
      this.node2 = node2;
      this.edge = edge;
    }

    @Override
    public int compareTo(FrontierEntry o) {
      return Double.compare(distance + penalty + remaining, o.distance + o.penalty + o.remaining);
    }
  }

  private static class Penalties {
    static final int STATION = 50;
    static final int STATION_WITH_TRAIN = 300;
    static final int MANUAL_TRAIN = 200;
    static final int IDLE_TRAIN = 700;
    static final int ARRIVING_TRAIN = 50;
    static final int WAITING_TRAIN = 50;
    static final int ANY_TRAIN = 25;
    static final int RED_SIGNAL = 25;
    static final int REDSTONE_RED_SIGNAL = 400;

    public Penalties() {
    }
  }
}
