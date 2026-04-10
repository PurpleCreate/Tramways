package purplecreate.tramways.content.signals.manual;

import com.simibubi.create.content.contraptions.actors.trainControls.ControlsHandler;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.signal.SignalBoundary;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import purplecreate.tramways.Tramways;
import purplecreate.tramways.content.signals.base.JunctionState;
import purplecreate.tramways.mixinInterfaces.IRoutedSignal;
import purplecreate.tramways.mixins.CarriageContraptionEntityAccessor;

import java.util.List;

public class RouteSelectionHUD {
  private static List<JunctionState> routes;
  private static int selectedRoute = 0;

  private static CarriageContraptionEntity getEntity() {
    if (ControlsHandler.getContraption() instanceof CarriageContraptionEntity cce)
      return cce;
    return null;
  }

  public static void prevRoute() {
    selectedRoute--;
    if (selectedRoute < 0) {
      selectedRoute = routes.size() - 1;
    }
  }

  public static void nextRoute() {
    selectedRoute++;
    if (selectedRoute >= routes.size()) {
      selectedRoute = 0;
    }
  }

  public static void promptForRoute(Player player, SignalBoundary signal, boolean forward) {
    if (!(getEntity() instanceof CarriageContraptionEntityAccessor ccea)) return;
    if (!(signal instanceof IRoutedSignal routed)) return;

    if (routes == null) {
      routes = routed.tramways$getPossibleRoutes(forward);
      selectedRoute = 0;
    }

    ccea.tramways$sendPrompt(
      player,
      Tramways.translatable(
        "contraption.controls.approach_junction",
        Component.keybind("key.left"),
        Component.keybind("key.right"),
        Component.keybind("key.jump"),
        routes.get(selectedRoute).getName()
      ),
      false
    );
  }
}
