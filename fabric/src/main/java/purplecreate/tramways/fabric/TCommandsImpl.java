package purplecreate.tramways.fabric;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import purplecreate.tramways.TCommands;

public class TCommandsImpl {
  public static void init() {
    ClientCommandRegistrationCallback.EVENT.register((dispatcher, context) ->
      TCommands.registerClientCommands((CommandDispatcher)dispatcher)
    );
  }
}
