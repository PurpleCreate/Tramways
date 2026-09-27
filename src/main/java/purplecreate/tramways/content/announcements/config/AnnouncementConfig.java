package purplecreate.tramways.content.announcements.config;

import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

import java.util.*;

public class AnnouncementConfig {
  private static final Random RANDOM = new Random();

  private final boolean train;
  private final Map<AnnouncementEvent, List<AnnouncementVariant>> eventVariants = new HashMap<>();

  public AnnouncementConfig(boolean train) {
    this.train = train;
  }

  public static AnnouncementConfig fromNbt(CompoundTag tag, boolean train) {
    AnnouncementConfig config = new AnnouncementConfig(train);

    CompoundTag eventsTag = tag.getCompound("Events");
    for (AnnouncementEvent event : AnnouncementEvent.values()) {
      config.eventVariants.put(event, NBTHelper.readCompoundList(
        eventsTag.getList(event.name(), Tag.TAG_COMPOUND),
        AnnouncementVariant::fromNbt
      ));
    }

    return config;
  }

  public CompoundTag toNbt() {
    CompoundTag tag = new CompoundTag();

    CompoundTag eventsTag = new CompoundTag();
    for (var entry : eventVariants.entrySet()) {
      if (entry.getValue().isEmpty()) continue;

      eventsTag.put(
        entry.getKey().name(),
        NBTHelper.writeCompoundList(entry.getValue(), AnnouncementVariant::toNbt)
      );
    }
    tag.put("Events", eventsTag);

    return tag;
  }

  public List<AnnouncementVariant> getVariants(AnnouncementEvent event) {
    List<AnnouncementVariant> variants = eventVariants.get(event);
    if (variants == null) return List.of();
    return variants;
  }

  public AnnouncementVariant getVariantFor(AnnouncementEvent event) {
    List<AnnouncementVariant> variants = getVariants(event);
    if (variants.isEmpty()) return null;
    return variants.get(RANDOM.nextInt(variants.size()));
  }

  public void addVariant(AnnouncementEvent event) {
    eventVariants
      .computeIfAbsent(event, k -> new ArrayList<>())
      .add(new AnnouncementVariant());
  }

  public void removeVariant(AnnouncementEvent event, AnnouncementVariant variant) {
    getVariants(event).remove(variant);
  }

  public boolean isTrainConfig() {
    return train;
  }
}
