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
import purplecreate.tramways.content.announcements.engine.tts.TTSVoices;
import purplecreate.tramways.content.announcements.engine.MP3AudioStream;
import purplecreate.tramways.content.announcements.engine.tts.TTSStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class PlayTTSInstruction extends AnnouncementInstruction {
  @Override
  public ResourceLocation getId() {
    return Tramways.rl("play_tts");
  }

  @Override
  public void initConfigurationWidgets(DLGuiComponent panel, CompoundTag data) {
    panel.layout.set(new FlowLayout());

    CreateTextBox content = panel.addComponent(new CreateTextBox(0, 0, 0));
    content.layoutContraint.set(FlowLayout.FlowConstraint.FILL);
    content.text.get().set(data.getString("Content"));
    content.tooltip.set(new DLTooltip(List.of(Tramways.translatable("announcements.instruction.play_tts.input").withStyle(ChatFormatting.GOLD)), 200));
    content.addEventListener(DLRichTextLabel.TextChangedEvent.class, (s, e) -> {
      data.putString("Content", content.text.get().getPlainText());
      return false;
    });
  }

  @Override
  public List<AudioStream> play(PlayContext context) throws IOException {
    TTSVoices.Voice voice = null;
    int iter = context.playIndex() - 1;
    while (voice == null && iter >= 0) {
      if (context.variant().get(iter) instanceof SetAnnouncerInstruction) {
        voice = SetAnnouncerInstruction.getVoice(context.variant().getData(iter));
      }
      iter--;
    }

    String content = context.data().getString("Content");
    if (context.isTrain()) {
      content = Variables.replace(context.trainData(), content);
    } else {
      content = Variables.replace(context.stationData(), content);
    }

    InputStream tts = new TTSStream(voice == null ? TTSVoices.DEFAULT_VOICE : voice, content);
    return List.of(new MP3AudioStream(tts));
  }
}
