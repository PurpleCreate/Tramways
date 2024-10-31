package purplecreate.tramways;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import purplecreate.tramways.compat.Mods;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.data.TagGen;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class TTags {
  public static final TagKey<Block> SIGNAL_POLE = createBlockTag("signal_pole");
  public static final TagKey<Block> SEMAPHORE_POLE = createBlockTag(Mods.RAILWAYS.id, "semaphore_poles");

  private static TagKey<Block> createBlockTag(String path) {
    return createBlockTag(Tramways.ID, path);
  }

  private static TagKey<Block> createBlockTag(String namespace, String path) {
    return TagKey.create(
      BuiltInRegistries.BLOCK.key(),
      new ResourceLocation(namespace, path)
    );
  }

  private static void genBlockTags(RegistrateTagsProvider<Block> p) {
    TagGen.CreateTagsProvider<Block> provider = new TagGen.CreateTagsProvider<>(p, Block::builtInRegistryHolder);

    provider.tag(SIGNAL_POLE)
      .add(
        TBlocks.TRAM_SIGNAL.get(),
        TBlocks.TRAM_SIGN.get(),
        TBlocks.RAILWAY_SIGN.get(),
        AllBlocks.METAL_GIRDER.get(),
        AllBlocks.METAL_GIRDER_ENCASED_SHAFT.get()
      )
      .addOptional(Mods.RAILWAYS.rl("semaphore"))
      .addTag(BlockTags.FENCES);

    provider.tag(SEMAPHORE_POLE)
      .add(
        TBlocks.TRAM_SIGNAL.get(),
        TBlocks.TRAM_SIGN.get(),
        TBlocks.RAILWAY_SIGN.get()
      );
  }

  public static void register() {
    Tramways.REGISTRATE.addDataGenerator(ProviderType.BLOCK_TAGS, TTags::genBlockTags);
  }
}
