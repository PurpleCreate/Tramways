package purplecreate.tramways.content.signals.manual;

import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.graph.DiscoveredPath;
import com.simibubi.create.content.trains.graph.EdgeData;
import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackNode;
import com.simibubi.create.content.trains.signal.SignalBlock;
import com.simibubi.create.content.trains.signal.SignalBoundary;
import com.simibubi.create.content.trains.signal.TrackEdgePoint;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.Pair;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import purplecreate.tramways.Tramways;
import purplecreate.tramways.content.signals.base.JunctionState;
import purplecreate.tramways.mixinInterfaces.IRoutedSignal;
import purplecreate.tramways.mixinInterfaces.ITramNavigation;
import purplecreate.tramways.mixins.CarriageContraptionEntityAccessor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RouteSelectionHUD {
  private final CarriageContraptionEntity cce;

  private final List<Pair<IRoutedSignal, Boolean>> notifiedSignals = new ArrayList<>();

  private List<JunctionState.SignalInfo> routes;
  private int selectedRoute = 0;

  private boolean messageShown = false;
  private boolean locked = false;

  private boolean jumpDownLast = false;
  private boolean leftDownLast = false;
  private boolean rightDownLast = false;

  public RouteSelectionHUD(CarriageContraptionEntity cce) {
    this.cce = cce;
  }

  public boolean control(boolean forward, Collection<Integer> heldControls, Player player) {
    Train train = cce.getCarriage().train;
    Pair<SignalBoundary, Boolean> signal = TrackPoints.findNearestJunction(train, forward);

    if (signal == null) {
      if (locked) {
        refreshRoutePrompt(player);
      } else {
        routes = null;
        selectedRoute = 0;
        clearPrompt(player);
      }

      leftDownLast = heldControls.contains(2);
      rightDownLast = heldControls.contains(3);
      jumpDownLast = heldControls.contains(4);

      return false;
    }

    ITramNavigation nav = ((ITramNavigation)train.navigation);
    IRoutedSignal routed = (IRoutedSignal)signal.getFirst();
    boolean signalForward = signal.getSecond();

    if (!locked) {
      routes = routed.tramways$getPossibleRoutes(signalForward);
      if (selectedRoute >= routes.size()) {
        selectedRoute = 0;
      }
    }

    boolean leftDown = heldControls.contains(2);
    boolean rightDown = heldControls.contains(3);
    boolean jumpDown = heldControls.contains(4);

    if (!jumpDown && jumpDownLast) {
      if (locked) {
        locked = false;
        nav.tramways$cancelRouteThroughJunction();
        notifiedSignals.forEach(pair -> {
          pair.getFirst().tramways$unnotifySelectedRoute(pair.getSecond(), train);
        });
      } else {
        locked = true;
        JunctionState.SignalInfo route = routes.get(selectedRoute);

        DiscoveredPath path = TrackPoints.findPathThroughJunction(train, route.signal(), forward);
        nav.tramways$setRouteThroughJunction(path, () -> {
          notifiedSignals.forEach(pair -> {
            pair.getFirst().tramways$unnotifySelectedRoute(pair.getSecond(), train);
          });
          locked = false;
        });

        List<Pair<SignalBoundary, Boolean>> signalPath = new ArrayList<>();
        signalPath.add(signal);

        everything:
        for (Couple<TrackNode> nodes : path.path) {
          TrackEdge edge = train.graph.getConnection(nodes);
          EdgeData data = edge.getEdgeData();
          if (!data.hasSignalBoundaries()) continue;

          for (TrackEdgePoint point : data.getPoints()) {
            if (!(point instanceof SignalBoundary boundary)) continue;
            boolean front = boundary.isPrimary(nodes.getSecond());
            if (route.signal() == boundary && route.forward() == front) break everything;
            signalPath.add(Pair.of(boundary, front));
          }
        }

        signalPath.add(route.asPair());

        for (int i = 0; i < signalPath.size(); i++) {
          Pair<SignalBoundary, Boolean> pair = signalPath.get(i);
          if (
            pair.getFirst().types.get(pair.getSecond()) != SignalBlock.SignalType.CROSS_SIGNAL
              || !(pair.getFirst() instanceof IRoutedSignal routedSignal)
          ) continue;

          routedSignal.tramways$notifySelectedRoute(
            pair.getSecond(),
            train,
            signalPath.subList(i, signalPath.size())
          );
          notifiedSignals.add(Pair.of(routedSignal, pair.getSecond()));
        }
      }
    }

    if (!leftDown && leftDownLast) {
      prevRoute();
    }

    if (!rightDown && rightDownLast) {
      nextRoute();
    }

    leftDownLast = heldControls.contains(2);
    rightDownLast = heldControls.contains(3);
    jumpDownLast = heldControls.contains(4);

    refreshRoutePrompt(player);
    return true;
  }

  private void prevRoute() {
    if (locked) return;

    selectedRoute--;
    if (selectedRoute < 0) {
      selectedRoute = routes.size() - 1;
    }
  }

  private void nextRoute() {
    if (locked) return;

    selectedRoute++;
    if (selectedRoute >= routes.size()) {
      selectedRoute = 0;
    }
  }

  private void refreshRoutePrompt(Player player) {
    MutableComponent message;

    if (locked) {
      message = Tramways.translatable(
        "contraption.controls.locked_route",
        routes.get(selectedRoute).state().getName(),
        Component.keybind("key.jump")
      );
    } else {
      message = Tramways.translatable(
        "contraption.controls.approach_junction",
        Component.keybind("key.left"),
        Component.keybind("key.right"),
        Component.keybind("key.jump"),
        routes.get(selectedRoute).state().getName()
      );
    }

    ((CarriageContraptionEntityAccessor)cce).tramways$sendPrompt(player, message, false);
    messageShown = true;
  }

  private void clearPrompt(Player player) {
    if (messageShown) {
      player.displayClientMessage(CommonComponents.EMPTY, true);
      messageShown = false;
    }
  }
}
