package purplecreate.tramways.content.announcements.config.instructions;

import de.mrjulsen.crn.data.train.portable.StationDisplayData;
import de.mrjulsen.crn.data.train.portable.TrainDisplayData;
import de.mrjulsen.mcdragonlib.client.gui.widgets.base.DLGuiComponent;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import purplecreate.tramways.content.announcements.config.AnnouncementVariant;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AnnouncementInstruction {
  private static final Map<ResourceLocation, AnnouncementInstruction> REGISTRY = new HashMap<>();

  public static List<ResourceLocation> getAll() {
    return REGISTRY.keySet().stream().toList();
  }

  public static AnnouncementInstruction get(ResourceLocation key) {
    return REGISTRY.get(key);
  }

  public static void register(AnnouncementInstruction value) {
    REGISTRY.put(value.getId(), value);
  }

  public abstract ResourceLocation getId();
  public abstract List<AudioStream> play(PlayContext context) throws IOException;
  public void initConfigurationWidgets(DLGuiComponent panel, CompoundTag data) {
  }

  public record PlayContext(
    AnnouncementVariant variant,
    int playIndex,
    CompoundTag data,
    TrainDisplayData trainData,
    StationDisplayData stationData
  ) {
    public boolean isTrain() {
      return trainData != null;
    }
  }
}
