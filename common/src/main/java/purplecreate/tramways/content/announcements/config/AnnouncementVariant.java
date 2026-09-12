package purplecreate.tramways.content.announcements.config;

import de.mrjulsen.crn.data.train.portable.StationDisplayData;
import de.mrjulsen.crn.data.train.portable.TrainDisplayData;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import purplecreate.tramways.content.announcements.config.instructions.AnnouncementInstruction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AnnouncementVariant {
  private final List<CompoundTag> instructions = new ArrayList<>();

  public static AnnouncementVariant fromNbt(CompoundTag tag) {
    AnnouncementVariant data = new AnnouncementVariant();
    NBTHelper.iterateCompoundList(tag.getList("Instructions", Tag.TAG_COMPOUND), data.instructions::add);
    return data;
  }

  public CompoundTag toNbt() {
    CompoundTag tag = new CompoundTag();
    tag.put("Instructions", NBTHelper.writeCompoundList(instructions, c -> c));
    return tag;
  }

  public int count() {
    return instructions.size();
  }

  public void add(ResourceLocation id, @Nullable CompoundTag data) {
    CompoundTag tag = new CompoundTag();
    tag.putString("Id", id.toString());
    tag.put("Data", data == null ? new CompoundTag() : data.copy());
    instructions.add(tag);
  }

  public void set(int i, @Nullable ResourceLocation id, @Nullable CompoundTag data) {
    CompoundTag tag = instructions.get(i);
    if (id != null && !tag.getString("Id").equals(id.toString())) {
      tag.putString("Id", id.toString());
      tag.put("Data", data == null ? new CompoundTag() : data.copy());
    } else if (data != null) {
      tag.put("Data", tag.getCompound("Data").merge(data));
    }
  }

  public void remove(int i) {
    instructions.remove(i);
  }

  public AnnouncementInstruction get(int i) {
    return AnnouncementInstruction.get(new ResourceLocation(instructions.get(i).getString("Id")));
  }

  public CompoundTag getData(int i) {
    return instructions.get(i).getCompound("Data");
  }

  public List<AudioStream> play(TrainDisplayData train, StationDisplayData station) throws IOException {
    List<AudioStream> streams = new ArrayList<>();

    for (int i = 0; i < count(); i++) {
      AnnouncementInstruction.PlayContext context = new AnnouncementInstruction.PlayContext(
        this, i, getData(i), train, station
      );

      AnnouncementInstruction instruction = get(i);
      List<AudioStream> stream = instruction.play(context);
      if (stream != null) streams.addAll(stream);
    }

    return streams;
  }
}
