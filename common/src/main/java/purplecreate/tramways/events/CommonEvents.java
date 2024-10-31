package purplecreate.tramways.events;

import net.minecraft.server.level.ServerPlayer;
import purplecreate.tramways.TNetworking;

public class CommonEvents {
  public static void onPlayerJoin(ServerPlayer player) {
    TNetworking.onPlayerJoin(player);
  }
}
