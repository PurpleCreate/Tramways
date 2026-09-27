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
import java.util.ArrayList;
import java.util.List;

public class PlayFileForEachInstruction extends AnnouncementInstruction {
  @Override
  public ResourceLocation getId() {
    return Tramways.rl("play_file_for_each");
  }

  @Override
  public void initConfigurationWidgets(DLGuiComponent panel, CompoundTag data) {
    panel.setHeight(18 + 2 + 18);

    FlowLayout layout = new FlowLayout();
    layout.flowDirection.set(FlowLayout.Direction.VERTICAL);
    layout.fillCrossAxis.set(true);
    layout.wrap.set(false);
    layout.verticalGap.set(2);
    panel.layout.set(layout);

    CreateTextBox variable = panel.addComponent(new CreateTextBox(0, 0, 0));
    variable.text.get().set(data.getString("Variable"));
    variable.tooltip.set(new DLTooltip(List.of(Tramways.translatable("announcements.instruction.play_file_for_each.input_1").withStyle(ChatFormatting.GOLD)), 200));
    variable.addEventListener(DLRichTextLabel.TextChangedEvent.class, (s, e) -> {
      data.putString("Variable", variable.text.get().getPlainText());
      return false;
    });

    CreateTextBox file = panel.addComponent(new CreateTextBox(0, 0, 0));
    file.text.get().set(data.getString("File"));
    file.tooltip.set(new DLTooltip(List.of(
      Tramways.translatable("announcements.instruction.play_file_for_each.input_2_1").withStyle(ChatFormatting.GOLD),
      Tramways.translatable("announcements.instruction.play_file_for_each.input_2_2").withStyle(ChatFormatting.GRAY)
    ), 200));
    file.addEventListener(DLRichTextLabel.TextChangedEvent.class, (s, e) -> {
      data.putString("File", file.text.get().getPlainText());
      return false;
    });
  }

  @Override
  public List<AudioStream> play(PlayContext context) throws IOException {
    ClientFileManager fm = ClientFileManager.getInstance();

    String variable = context.data().getString("Variable");
    String baseName = context.data().getString("File");

    List<String> names;
    if (context.isTrain()) {
      names = Variables.iterate(context.trainData(), variable, baseName);
    } else {
      names = Variables.iterate(context.stationData(), variable, baseName);
    }

    List<AudioStream> streams = new ArrayList<>();
    for (String name : names) {
      FileInfo info = fm.getFile(name);
      if (info == null) continue;

      InputStream stream;
      try {
        stream = new FileInputStream(info.getRealLocation(fm.getFolder()));
      } catch (IOException e) {
        Tramways.LOGGER.warn("An IOException occurred whilst playing {}", name, e);
        continue;
      }

      streams.add(info.fileType().createAudioStream(stream));
    }

    return streams;
  }
}
