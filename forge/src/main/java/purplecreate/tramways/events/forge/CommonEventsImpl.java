package purplecreate.tramways.events.forge;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
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
  public static void onLevelTick(TickEvent.LevelTickEvent event) {
    if (event.phase == TickEvent.Phase.START)
      return;
    if (event.side == LogicalSide.CLIENT)
      return;
    CommonEvents.onLevelTick(event.level);
  }
}
