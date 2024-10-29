package purplecreate.tramways.events;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import purplecreate.tramways.content.announcements.network.PlayMovingVoiceS2CPacket;
import purplecreate.tramways.content.announcements.network.PlayVoiceS2CPacket;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientEvents {
  @SubscribeEvent
  public static void onTick(TickEvent.ClientTickEvent event) {
    if (!isGameActive())
      return;

    PlayMovingVoiceS2CPacket.tick();
    PlayVoiceS2CPacket.tick();
  }

  protected static boolean isGameActive() {
    return !(Minecraft.getInstance().level == null || Minecraft.getInstance().player == null);
  }
}
