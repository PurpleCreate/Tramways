package purplecreate.tramways;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.mrjulsen.mcdragonlib.client.gui.widgets.base.DLWindow;
import net.minecraft.commands.SharedSuggestionProvider;
import purplecreate.tramways.content.announcements.config.gui.FileManagerWindow;

public class TCommands {
  public static void registerClientCommands(CommandDispatcher<SharedSuggestionProvider> dispatcher) {
    dispatcher.register(
      literal(Tramways.ID)
        .requires(stack -> stack.hasPermission(0))
        .then(
          literal("filemanager")
            .executes(stack -> {
              DLWindow.openWindow(m -> new FileManagerWindow(m, FileManagerWindow.forceAdminMode()));
              return 1;
            })
            .then(
              literal("admin")
                .requires(stack -> stack.hasPermission(2))
                .executes(stack -> {
                  DLWindow.openWindow(m -> new FileManagerWindow(m, true));
                  return 1;
                })
            )
        )
    );
  }

  public static LiteralArgumentBuilder<SharedSuggestionProvider> literal(String name) {
    return LiteralArgumentBuilder.literal(name);
  }
}
