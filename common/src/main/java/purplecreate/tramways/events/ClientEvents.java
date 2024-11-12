package purplecreate.tramways.events;

import net.minecraft.client.Minecraft;
import purplecreate.tramways.content.requestStop.RequestStopClient;

public class ClientEvents {
  public static void onClientTickStart(Minecraft mc) {
    if (!isGameActive())
      return;

    RequestStopClient.tick(mc);
  }

  protected static boolean isGameActive() {
    return !(Minecraft.getInstance().level == null || Minecraft.getInstance().player == null);
  }
}