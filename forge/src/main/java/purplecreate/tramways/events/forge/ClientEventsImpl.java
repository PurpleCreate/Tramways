package purplecreate.tramways.events.forge;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import purplecreate.tramways.events.ClientEvents;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientEventsImpl {
  @SubscribeEvent
  public static void onClientTick(TickEvent.ClientTickEvent event) {
    if (event.phase == TickEvent.Phase.START)
      ClientEvents.onClientTickStart(Minecraft.getInstance());
  }
}