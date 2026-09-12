package purplecreate.tramways.events;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import purplecreate.tramways.TNetworking;
import purplecreate.tramways.content.announcements.engine.files.ServerFileManager;

public class CommonEvents {
  public static void onPlayerJoin(ServerPlayer player) {
    TNetworking.onPlayerJoin(player);
    ServerFileManager.getInstance(player.server).handlePlayerJoin(player);
  }

  public static void onServerTick(MinecraftServer server) {
    ServerFileManager.getInstance(server).tick();
  }

  public static void onServerStarting(MinecraftServer server) {
    ServerFileManager.getInstance(server).init();
  }

  public static void onServerStopping(MinecraftServer server) {
    ServerFileManager.getInstance(server).destroy();
  }

  public static void onLevelSave(ServerLevel level) {
    ServerFileManager.getInstance(level.getServer()).save();
  }
}
