package purplecreate.tramways;

import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.simibubi.create.foundation.utility.Components;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import purplecreate.tramways.config.Config;

public class TCommands {
  public static class ReloadConfigCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
      return Commands.literal("reloadConfig")
        .requires((stack) -> stack.hasPermission(2))
        .executes(ReloadConfigCommand::execute);
    }

    private static int execute(CommandContext<CommandSourceStack> context) {
      try {
        Config.reload();
        context.getSource().sendSuccess(
          () -> Components.literal("Reloaded config!"),
          true
        );
        return 1;
      } catch (JsonSyntaxException e) {
        context.getSource().sendFailure(
          Components.literal("Couldn't reload config! " + e.getMessage())
        );
        e.printStackTrace();
        return 0;
      }
    }
  }

  public static void register(CommandDispatcher<CommandSourceStack> dispatcher, boolean dedicated) {
    dispatcher.register(
      Commands.literal(Tramways.ID)
        .requires((stack) -> stack.hasPermission(0))
        .then(ReloadConfigCommand.register())
    );
  }
}
