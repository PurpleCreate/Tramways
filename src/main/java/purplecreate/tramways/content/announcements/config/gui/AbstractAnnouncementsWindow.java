package purplecreate.tramways.content.announcements.config.gui;

import com.simibubi.create.foundation.gui.AllIcons;
import de.mrjulsen.crn.client.gui.widgets.create.CreateButton;
import de.mrjulsen.mcdragonlib.client.gui.events.DLGuiStandardEvents;
import de.mrjulsen.mcdragonlib.client.gui.widgets.base.DLWindow;
import de.mrjulsen.mcdragonlib.client.gui.widgets.base.DLWindowManager;
import net.minecraft.nbt.CompoundTag;
import purplecreate.tramways.Tramways;
import purplecreate.tramways.content.announcements.config.AnnouncementConfig;

import java.util.function.Consumer;

public class AbstractAnnouncementsWindow extends AbstractTitledWindow {
  protected final AnnouncementConfig config;
  protected final Consumer<CompoundTag> onClose;

  public AbstractAnnouncementsWindow(DLWindowManager manager, AnnouncementConfig config, Consumer<CompoundTag> onClose) {
    super(manager, Tramways.translatable("announcements.title"));
    this.config = config;
    this.onClose = onClose;

    setSize(GUI_WIDTH, GUI_HEIGHT);
    manager.setPauseScreen(false);
    windowSpawnPosition.set(DLWindow.WindowPosition.CENTER);

    addComponent(new CreateButton(8, 223, AllIcons.I_CONFIG_BACK))
      .addEventListener(DLGuiStandardEvents.ClickEvent.class, (s, e) -> {
        getWindowManager().closeWindow(this);
        return false;
      });
  }

  @Override
  public void close() throws Exception {
    super.close();
    onClose.accept(config.toNbt());
  }
}
