package purplecreate.tramways;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.mrjulsen.mcdragonlib.client.gui.widgets.base.DLWindow;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import purplecreate.tramways.content.announcements.config.gui.FileManagerWindow;

@EventBusSubscriber
public class TCommands {
  @SubscribeEvent
  public static void registerClientCommands(RegisterClientCommandsEvent event) {
    event.getDispatcher().register(
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

  public static LiteralArgumentBuilder<CommandSourceStack> literal(String name) {
    return LiteralArgumentBuilder.literal(name);
  }
}
