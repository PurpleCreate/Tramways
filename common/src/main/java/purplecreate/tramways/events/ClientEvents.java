package purplecreate.tramways.events;

import net.minecraft.client.Minecraft;
import purplecreate.tramways.content.announcements.sound.MinimalSoundEngine;
import purplecreate.tramways.content.requestStop.RequestStopClient;

public class ClientEvents {
  private static boolean lastPauseState = Minecraft.getInstance().isPaused();

  public static void onClientTickStart(Minecraft mc) {
    if (!isGameActive())
      return;

    RequestStopClient.tick(mc);
    MinimalSoundEngine.tick();

    boolean paused = mc.isPaused();
    if (paused != lastPauseState) {
      lastPauseState = paused;
      ClientEvents.onClientPauseChange(paused);
    }
  }

  public static void onClientPauseChange(boolean paused) {
    if (paused)
      MinimalSoundEngine.pauseAll();
    else
      MinimalSoundEngine.resumeAll();
  }

  public static void onLeave() {
    MinimalSoundEngine.stopAll();
  }

  protected static boolean isGameActive() {
    return !(Minecraft.getInstance().level == null || Minecraft.getInstance().player == null);
  }
}