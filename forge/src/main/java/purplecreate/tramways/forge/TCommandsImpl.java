package purplecreate.tramways.forge;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import purplecreate.tramways.TCommands;

@Mod.EventBusSubscriber
public class TCommandsImpl {
  @SubscribeEvent
  public static void onCommandRegistration(RegisterClientCommandsEvent event) {
    TCommands.registerClientCommands((CommandDispatcher)event.getDispatcher());
  }
}
