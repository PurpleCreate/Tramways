package purplecreate.tramways.events.fabric;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import purplecreate.tramways.events.CommonEvents;

public class CommonEventsImpl {
  public static void register() {
    ServerPlayConnectionEvents.JOIN.register((listener, sender, server) ->
      CommonEvents.onPlayerJoin(listener.player)
    );

    ServerTickEvents.END_SERVER_TICK.register(CommonEvents::onServerTick);
    ServerLifecycleEvents.SERVER_STARTING.register(CommonEvents::onServerStarting);
    ServerLifecycleEvents.SERVER_STOPPING.register(CommonEvents::onServerStopping);
  }
}
