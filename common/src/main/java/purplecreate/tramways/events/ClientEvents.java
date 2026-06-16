package purplecreate.tramways.events;

import net.minecraft.client.Minecraft;
import purplecreate.tramways.content.announcements.engine.SoundEngine;
import purplecreate.tramways.content.announcements.engine.files.ClientFileManager;
import purplecreate.tramways.content.requestStop.RequestStopClient;

public class ClientEvents {
  private static boolean lastPauseState = Minecraft.getInstance().isPaused();

  public static void onClientTickStart(Minecraft mc) {
    if (!isGameActive())
      return;

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

  public static void onJoin() {
    ClientFileManager.getInstance().init();
  }

  public static void onLeave() {
    SoundEngine.clean();
    ClientFileManager.getInstance().destroy();
  }

  protected static boolean isGameActive() {
    return !(Minecraft.getInstance().level == null || Minecraft.getInstance().player == null);
  }
}