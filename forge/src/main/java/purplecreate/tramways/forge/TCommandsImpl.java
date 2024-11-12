package purplecreate.tramways.forge;

import net.minecraft.commands.Commands;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import purplecreate.tramways.TCommands;

@Mod.EventBusSubscriber
public class TCommandsImpl {
  @SubscribeEvent
  public static void onCommandRegistration(RegisterCommandsEvent event) {
    Commands.CommandSelection selection = event.getCommandSelection();
    boolean dedicated = selection == Commands.CommandSelection.ALL
      || selection == Commands.CommandSelection.DEDICATED;
    TCommands.register(event.getDispatcher(), dedicated);
  }
}
