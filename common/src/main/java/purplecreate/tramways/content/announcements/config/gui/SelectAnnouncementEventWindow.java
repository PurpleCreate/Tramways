package purplecreate.tramways.content.announcements.config.gui;

import com.simibubi.create.foundation.gui.AllIcons;
import de.mrjulsen.crn.client.gui.CreateDynamicWidgets.FooterSize;
import de.mrjulsen.crn.client.gui.widgets.create.CreateButton;
import de.mrjulsen.crn.client.gui.widgets.options.OptionEntryHeader;
import de.mrjulsen.crn.client.gui.widgets.options.OptionsView;
import de.mrjulsen.mcdragonlib.client.gui.events.DLGuiStandardEvents;
import de.mrjulsen.mcdragonlib.client.gui.widgets.base.DLWindowManager;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import purplecreate.tramways.Tramways;
import purplecreate.tramways.content.announcements.config.AnnouncementConfig;
import purplecreate.tramways.content.announcements.config.AnnouncementEvent;

import java.util.List;
import java.util.function.Consumer;

public class SelectAnnouncementEventWindow extends AbstractAnnouncementsWindow {
  public SelectAnnouncementEventWindow(DLWindowManager manager, AnnouncementConfig config, Consumer<CompoundTag> onClose) {
    super(manager, config, onClose);

    OptionsView view = addComponent(new OptionsView(
      3,
      FooterSize.DEFAULT.size() + 1,
      GUI_WIDTH - 6,
      GUI_HEIGHT - (FooterSize.DEFAULT.size() + 1) - FooterSize.SMALL.size() - 1
    ));

    for (AnnouncementEvent event : AnnouncementEvent.values()) {
      if (event.isTrainEvent() != config.isTrainConfig()) continue;

      int variants = config.getVariants(event).size();

      view.addEntry(new OptionEntryHeader(event.translatable(), List.of(
        event.translatable(),
        Tramways.translatable("announcements.variant_count." + (variants == 1 ? "singular" : "plural"), variants)
          .withStyle(ChatFormatting.GRAY)
      )))
        .addEventListener(DLGuiStandardEvents.ClickEvent.class, (s, e) -> {
          getWindowManager().createModal(m -> new SetupAnnouncementVariantsWindow(m, config, onClose, event));
          return false;
        });
    }

    addComponent(new CreateButton(this.width() - 18 - 8, 223, AllIcons.I_OPEN_FOLDER))
      .addEventListener(DLGuiStandardEvents.ClickEvent.class, (s, e) -> {
        getWindowManager().createModal(m -> new FileManagerWindow(m, FileManagerWindow.forceAdminMode()));
        return false;
      });
  }
}
