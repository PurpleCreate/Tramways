package purplecreate.tramways.neoforge;

import net.minecraft.commands.Commands;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import purplecreate.tramways.TCommands;

@EventBusSubscriber
public class TCommandsImpl {
  @SubscribeEvent
  public static void onCommandRegistration(RegisterCommandsEvent event) {
    Commands.CommandSelection selection = event.getCommandSelection();
    boolean dedicated = selection == Commands.CommandSelection.ALL
      || selection == Commands.CommandSelection.DEDICATED;
    TCommands.register(event.getDispatcher(), dedicated);
  }
}
