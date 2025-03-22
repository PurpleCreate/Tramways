package purplecreate.tramways.events.fabric;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import purplecreate.tramways.events.CommonEvents;

public class CommonEventsImpl {
  public static void register() {
    ServerPlayConnectionEvents.JOIN.register((listener, sender, server) ->
      CommonEvents.onPlayerJoin(listener.player)
    );

    ServerTickEvents.END_WORLD_TICK.register(CommonEvents::onLevelTick);
  }
}
