package purplecreate.tramways.mixinInterfaces;

import net.minecraft.nbt.CompoundTag;
import purplecreate.tramways.content.announcements.config.AnnouncementConfig;

import java.util.UUID;

public interface ITram {
  boolean tramways$hasTemporaryLimit();

  double tramways$getPrimaryLimit();
  void tramways$setPrimaryLimit(double limit);

  void tramways$putSign(UUID id, boolean primary, double distance);
  void tramways$clearSigns();

  AnnouncementConfig tramways$getAnnouncementConfig();
  void tramways$setAnnouncementConfig(AnnouncementConfig config);

  void tramways$read(CompoundTag tag);
}
