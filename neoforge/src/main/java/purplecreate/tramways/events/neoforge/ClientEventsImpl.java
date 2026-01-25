package purplecreate.tramways.events.neoforge;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import purplecreate.tramways.content.stationDeco.nameSign.info.NameSignInfo;
import purplecreate.tramways.events.ClientEvents;

@EventBusSubscriber(Dist.CLIENT)
public class ClientEventsImpl {
  @SubscribeEvent
  public static void onClientTick(ClientTickEvent.Pre event) {
    ClientEvents.onClientTickStart(Minecraft.getInstance());
  }

  @SubscribeEvent
  public static void onLeave(ClientPlayerNetworkEvent.LoggingOut event) {
    ClientEvents.onLeave();
  }

  @EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
  public static class ModEvents {
    @SubscribeEvent
    public static void registerClientReloadListeners(RegisterClientReloadListenersEvent event) {
      event.registerReloadListener(NameSignInfo.listener);
    }
  }
}