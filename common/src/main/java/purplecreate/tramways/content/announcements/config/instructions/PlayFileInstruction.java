package purplecreate.tramways.content.announcements.config.instructions;

import de.mrjulsen.crn.client.gui.widgets.create.CreateTextBox;
import de.mrjulsen.mcdragonlib.client.gui.widgets.base.DLGuiComponent;
import de.mrjulsen.mcdragonlib.client.gui.widgets.components.DLRichTextLabel;
import de.mrjulsen.mcdragonlib.client.gui.widgets.components.DLTooltip;
import de.mrjulsen.mcdragonlib.client.gui.widgets.layout.FlowLayout;
import net.minecraft.ChatFormatting;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import purplecreate.tramways.Tramways;
import purplecreate.tramways.content.announcements.config.Variables;
import purplecreate.tramways.content.announcements.engine.files.ClientFileManager;
import purplecreate.tramways.content.announcements.engine.files.FileInfo;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class PlayFileInstruction extends AnnouncementInstruction {
  @Override
  public ResourceLocation getId() {
    return Tramways.rl("play_file");
  }

  @Override
  public void initConfigurationWidgets(DLGuiComponent panel, CompoundTag data) {
    panel.layout.set(new FlowLayout());

    CreateTextBox file = panel.addComponent(new CreateTextBox(0, 0, 0));
    file.layoutContraint.set(FlowLayout.FlowConstraint.FILL);
    file.text.get().set(data.getString("File"));
    file.tooltip.set(new DLTooltip(List.of(Tramways.translatable("announcements.instruction.play_file.input").withStyle(ChatFormatting.GOLD)), 200));
    file.addEventListener(DLRichTextLabel.TextChangedEvent.class, (s, e) -> {
      data.putString("File", file.text.get().getPlainText());
      return false;
    });
  }

  @Override
  public List<AudioStream> play(PlayContext context) throws IOException {
    String name = context.data().getString("File");
    if (context.isTrain()) {
      name = Variables.replace(context.trainData(), name);
    } else {
      name = Variables.replace(context.stationData(), name);
    }

    ClientFileManager fm = ClientFileManager.getInstance();
    FileInfo info = fm.getFile(name);

    if (info == null) return null;

    InputStream stream;
    try {
      stream = new FileInputStream(info.getRealLocation(fm.getFolder()));
    } catch (IOException e) {
      Tramways.LOGGER.warn("An IOException occurred whilst playing {}", name, e);
      return null;
    }

    return List.of(info.fileType().createAudioStream(stream));
  }
}
