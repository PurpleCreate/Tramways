package purplecreate.tramways.events;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import purplecreate.tramways.TNetworking;

public class CommonEvents {
  public static void onPlayerJoin(ServerPlayer player) {
    TNetworking.onPlayerJoin(player);
  }

  public static void onLevelTick(Level level) {

  }
}
