package purplecreate.tramways.content.announcements.config.instructions;

import de.mrjulsen.crn.client.gui.widgets.create.CreateItemPicker;
import de.mrjulsen.mcdragonlib.client.gui.widgets.base.DLGuiComponent;
import de.mrjulsen.mcdragonlib.client.gui.widgets.components.DLCycleButton;
import de.mrjulsen.mcdragonlib.client.gui.widgets.layout.FlowLayout;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import purplecreate.tramways.Tramways;
import purplecreate.tramways.content.announcements.engine.tts.TTSVoices;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class SetAnnouncerInstruction extends AnnouncementInstruction {
  public static TTSVoices.Voice getVoice(CompoundTag tag) {
    return TTSVoices.fromId(tag.getString("Voice")).orElse(TTSVoices.DEFAULT_VOICE);
  }

  @Override
  public ResourceLocation getId() {
    return Tramways.rl("set_announcer");
  }

  @Override
  public void initConfigurationWidgets(DLGuiComponent panel, CompoundTag data) {
    panel.layout.set(new FlowLayout());

    CreateItemPicker<TTSVoices.Voice> voice = panel.addComponent(new CreateItemPicker<>(0, 0, 0));
    voice.layoutContraint.set(FlowLayout.FlowConstraint.FILL);
    voice.formatter.set(v -> Component.literal(v.name()));
    voice.items.addAll(TTSVoices.VALUES);
    voice.selectedItem.set(Optional.of(getVoice(data)));
    voice.addEventListener(DLCycleButton.SelectedItemChanged.class, (s, e) -> {
      data.putString("Voice", voice.selectedItem.get().orElse(TTSVoices.DEFAULT_VOICE).id());
      return false;
    });
  }

  @Override
  public List<AudioStream> play(PlayContext context) {
    return null;
  }
}
