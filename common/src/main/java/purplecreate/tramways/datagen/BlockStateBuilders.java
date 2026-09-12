package purplecreate.tramways.datagen;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import purplecreate.tramways.content.signs.SignAttachedToPoleBlock;

public class BlockStateBuilders {
  public interface BlockConsumer<T extends Block> extends NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> {}
  public interface ItemConsumer<T extends Item> extends NonNullBiConsumer<DataGenContext<Item, T>, RegistrateItemModelProvider> {}

  @ExpectPlatform
  public static <T extends SignAttachedToPoleBlock> BlockConsumer<T> empty() {
    throw new AssertionError();
  }

  @ExpectPlatform
  public static <T extends Block> BlockConsumer<T> stationNameSign(String color) {
    throw new AssertionError();
  }

  @ExpectPlatform
  public static <T extends Item> ItemConsumer<T> stationNameSignItem(String color) {
    throw new AssertionError();
  }

  @ExpectPlatform
  public static <T extends Block> BlockConsumer<T> horizontalBlock(String existingModelPath) {
    throw new AssertionError();
  }

  @ExpectPlatform
  public static <T extends Block> BlockConsumer<T> directionalBlock(String existingModelPath) {
    throw new AssertionError();
  }

  @ExpectPlatform
  public static <T extends ButtonBlock> BlockConsumer<T> buttonBlock(ResourceLocation texture) {
    throw new AssertionError();
  }
}
