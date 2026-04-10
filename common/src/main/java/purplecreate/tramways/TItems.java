package purplecreate.tramways;

import com.tterrag.registrate.util.entry.ItemEntry;

import purplecreate.tramways.content.signals.routing.RouteConfiguratorItem;

public class TItems {
  public static final ItemEntry<RouteConfiguratorItem> ROUTE_CONFIGURATOR =
    Tramways.REGISTRATE.item("route_configurator", RouteConfiguratorItem::new)
      .lang("Route Configurator")
      .register();

  public static void register() {}
}
