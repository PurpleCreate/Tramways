package purplecreate.tramways.events.forge;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import purplecreate.tramways.events.CommonEvents;

@Mod.EventBusSubscriber
public class CommonEventsImpl {
  @SubscribeEvent
  public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
    if (event.getEntity() instanceof ServerPlayer player)
      CommonEvents.onPlayerJoin(player);
  }

  @SubscribeEvent
  public static void onServerTick(TickEvent.ServerTickEvent event) {
    if (event.phase == TickEvent.Phase.START)
      return;
    CommonEvents.onServerTick(event.getServer());
  }

  @SubscribeEvent
  public static void onServerStarting(ServerStartingEvent event) {
    CommonEvents.onServerStarting(event.getServer());
  }

  @SubscribeEvent
  public static void onServerStopping(ServerStoppingEvent event) {
    CommonEvents.onServerStopping(event.getServer());
  }
}
