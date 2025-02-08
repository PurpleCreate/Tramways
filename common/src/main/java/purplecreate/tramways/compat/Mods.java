package purplecreate.tramways.compat;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Locale;
import java.util.function.Supplier;

public enum Mods {
  CREATERAILWAYSNAVIGATOR,
  RAILWAYS;

  public final String id;

  Mods() {
    id = name().toLowerCase(Locale.ROOT);
  }

  @ExpectPlatform
  public static boolean modLoaded(String id) {
    throw new AssertionError();
  }

  public boolean loaded() {
    return modLoaded(id);
  }

  public void ifLoadedRun(Supplier<Runnable> supplier) {
    if (loaded())
      supplier.get().run();
  }

  public ResourceLocation rl(String path) {
    return new ResourceLocation(id, path);
  }

  public Block getBlock(String path) {
    return BuiltInRegistries.BLOCK.get(rl(path));
  }

  public boolean isBlock(String path, BlockState state) {
    return isBlock(path, state.getBlock());
  }

  public boolean isBlock(String path, Block block) {
    return loaded() && getBlock(path) == block;
  }
}
