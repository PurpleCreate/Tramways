package purplecreate.tramways.compat;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Locale;

public enum Mods {
  RAILWAYS;

  public final String id;

  Mods() {
    id = name().toLowerCase(Locale.ROOT);
  }

  public boolean loaded() {
    return ModList.get().isLoaded(id);
  }

  public ResourceLocation rl(String path) {
    return new ResourceLocation(id, path);
  }

  public Block getBlock(String path) {
    return ForgeRegistries.BLOCKS.getValue(rl(path));
  }

  public boolean isBlock(String path, BlockState state) {
    return isBlock(path, state.getBlock());
  }

  public boolean isBlock(String path, Block block) {
    return loaded() && getBlock(path) == block;
  }
}
