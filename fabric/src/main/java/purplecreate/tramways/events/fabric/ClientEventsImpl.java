package purplecreate.tramways.events.fabric;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import purplecreate.tramways.events.ClientEvents;

public class ClientEventsImpl {
  public static void register() {
    ClientTickEvents.START_CLIENT_TICK.register(ClientEvents::onClientTickStart);
  }
}