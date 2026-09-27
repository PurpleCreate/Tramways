package purplecreate.tramways.events;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import purplecreate.tramways.content.announcements.engine.SoundEngine;
import purplecreate.tramways.content.announcements.engine.files.ClientFileManager;
import purplecreate.tramways.content.requestStop.RequestStopClient;
import purplecreate.tramways.content.stationDeco.nameSign.info.NameSignInfo;

@EventBusSubscriber(Dist.CLIENT)
public class ClientEvents {
  private static boolean lastPauseState;

  static {
    Minecraft mc = Minecraft.getInstance();
    if (mc != null) {
      lastPauseState = mc.isPaused();
    }
  }

  @SubscribeEvent
  public static void onClientTickStart(ClientTickEvent.Pre event) {
    if (!isGameActive())
      return;

    Minecraft mc = Minecraft.getInstance();

    RequestStopClient.tick(mc);
    SoundEngine.tick();
    ClientFileManager.getInstance().tick();

    boolean paused = mc.isPaused();
    if (paused != lastPauseState) {
      lastPauseState = paused;
      ClientEvents.onClientPauseChange(paused);
    }
  }

  public static void onClientPauseChange(boolean paused) {
    if (paused)
      SoundEngine.pauseAll();
    else
      SoundEngine.resumeAll();
  }

  @SubscribeEvent
  public static void onJoin(ClientPlayerNetworkEvent.LoggingIn event) {
    ClientFileManager.getInstance().init();
  }

  @SubscribeEvent
  public static void onLeave(ClientPlayerNetworkEvent.LoggingOut event) {
    SoundEngine.clean();
    ClientFileManager.getInstance().destroy();
  }

  @SubscribeEvent
  public static void registerClientReloadListeners(RegisterClientReloadListenersEvent event) {
    event.registerReloadListener(NameSignInfo.listener);
  }

  protected static boolean isGameActive() {
    return !(Minecraft.getInstance().level == null || Minecraft.getInstance().player == null);
  }
}