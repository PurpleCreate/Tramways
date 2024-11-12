package purplecreate.tramways.fabric;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import purplecreate.tramways.TCommands;

public class TCommandsImpl {
  public static void init() {
    CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
      TCommands.register(dispatcher, environment.includeDedicated)
    );
  }
}
