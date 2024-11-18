package purplecreate.tramways.events.fabric;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;
import purplecreate.tramways.content.stationDeco.nameSign.info.NameSignInfo;
import purplecreate.tramways.events.ClientEvents;

public class ClientEventsImpl {
  public static void register() {
    ClientTickEvents.START_CLIENT_TICK.register(ClientEvents::onClientTickStart);

    ResourceManagerHelper.get(PackType.CLIENT_RESOURCES)
      .registerReloadListener((IdentifiableResourceReloadListener) NameSignInfo.listener);
  }
}