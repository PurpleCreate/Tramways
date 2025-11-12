package purplecreate.tramways.mixinInterfaces;

import net.minecraft.nbt.CompoundTag;

import java.util.UUID;

public interface ITram {
  boolean tramways$hasTemporaryLimit();

  double tramways$getPrimaryLimit();
  void tramways$setPrimaryLimit(double limit);

  void tramways$putSign(UUID id, boolean primary, double distance);

  void tramways$read(CompoundTag tag);
}
