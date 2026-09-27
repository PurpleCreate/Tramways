package purplecreate.tramways.events;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import purplecreate.tramways.content.announcements.engine.files.ServerFileManager;

@EventBusSubscriber
public class CommonEvents {
  @SubscribeEvent
  public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
    if (event.getEntity() instanceof ServerPlayer player) {
      ServerFileManager.getInstance(player.server).handlePlayerJoin(player);
    }
  }

  @SubscribeEvent
  public static void onServerTick(ServerTickEvent.Post event) {
    ServerFileManager.getInstance(event.getServer()).tick();
  }

  @SubscribeEvent
  public static void onServerStarting(ServerStartingEvent event) {
    ServerFileManager.getInstance(event.getServer()).init();
  }

  @SubscribeEvent
  public static void onServerStopping(ServerStoppingEvent event) {
    ServerFileManager.getInstance(event.getServer()).destroy();
  }

  public static void onLevelSave(ServerLevel level) {
    ServerFileManager.getInstance(level.getServer()).save();
  }
}
