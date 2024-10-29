package purplecreate.tramways;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.tags.ITagManager;
import purplecreate.tramways.compat.Mods;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.data.TagGen;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collections;

public class TTags {
  public static final TagKey<Block> SIGNAL_POLE = createBlockTag("signal_pole");
  public static final TagKey<Block> SEMAPHORE_POLE = createBlockTag(Mods.RAILWAYS.id, "semaphore_poles");

  private static TagKey<Block> createBlockTag(String path) {
    return createBlockTag(Tramways.ID, path);
  }

  private static TagKey<Block> createBlockTag(String namespace, String path) {
    ITagManager<Block> manager = ForgeRegistries.BLOCKS.tags();
    if (manager == null) return null;
    return manager.createOptionalTagKey(
      new ResourceLocation(namespace, path),
      Collections.emptySet()
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
