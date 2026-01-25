package purplecreate.tramways.util;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

public class NBT {
  public static BlockPos readBlockPosOld(CompoundTag nbt) {
    return new BlockPos(nbt.getInt("X"), nbt.getInt("Y"), nbt.getInt("Z"));
  }

  public static CompoundTag writeBlockPosOld(BlockPos pos) {
    CompoundTag compoundTag = new CompoundTag();
    compoundTag.putInt("X", pos.getX());
    compoundTag.putInt("Y", pos.getY());
    compoundTag.putInt("Z", pos.getZ());
    return compoundTag;
  }
}
