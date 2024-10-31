package purplecreate.tramways.events;

import net.minecraft.client.Minecraft;
import purplecreate.tramways.util.QueuedPacket;

public class ClientEvents {
  public static void onClientTickStart(Minecraft mc) {
    if (!isGameActive())
      return;

    QueuedPacket.tick();
  }

  protected static boolean isGameActive() {
    return !(Minecraft.getInstance().level == null || Minecraft.getInstance().player == null);
  }
}
