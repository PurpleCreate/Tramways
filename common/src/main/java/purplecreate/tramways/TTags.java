package purplecreate.tramways;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import purplecreate.tramways.compat.Mods;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.data.TagGen;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import purplecreate.tramways.content.signals.block.SignAttachedToPoleBlock;

public class TTags {
  public static final TagKey<Item> NAME_SIGN = createItemTag("name_sign");
  public static final TagKey<Block> SIGNAL_POLE = createBlockTag("signal_pole");
  public static final TagKey<Block> ATTACHMENT_POLE_ALL = createBlockTag("attachment_pole/all");
  public static final TagKey<Block> NAME_SIGN_INNER = createBlockTag("name_sign_inner");
  public static final TagKey<Block> SEMAPHORE_POLE = createBlockTag(Mods.RAILWAYS.id, "semaphore_poles");

  private static TagKey<Item> createItemTag(String path) {
    return createItemTag(Tramways.ID, path);
  }

  private static TagKey<Item> createItemTag(String namespace, String path) {
    return TagKey.create(
      BuiltInRegistries.ITEM.key(),
      new ResourceLocation(namespace, path)
    );
  }

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
      .add(AllBlocks.METAL_GIRDER_ENCASED_SHAFT.get())
      .addOptional(Mods.RAILWAYS.rl("semaphore"))
      .addTag(ATTACHMENT_POLE_ALL);

    provider.tag(NAME_SIGN_INNER)
      .add(Blocks.NETHER_BRICKS)
      .addTag(BlockTags.PLANKS);

    var attachmentPoleBuilder = provider.tag(ATTACHMENT_POLE_ALL);
    for (int i = 1; i < 17; i++) {
      provider.tag(SignAttachedToPoleBlock.tagForPoleSized(i));
      attachmentPoleBuilder.addTag(SignAttachedToPoleBlock.tagForPoleSized(i));
    }

    provider.tag(SignAttachedToPoleBlock.tagForPoleSized(4))
      .addTag(BlockTags.FENCES);

    provider.tag(SignAttachedToPoleBlock.tagForPoleSized(8))
      .add(AllBlocks.METAL_GIRDER.get())
      .addTag(BlockTags.WALLS);
  }

  public static TagKey<Block>[] pole() {
    return new TagKey[]{SEMAPHORE_POLE, SIGNAL_POLE};
  }

  public static void register() {
    Tramways.REGISTRATE.addDataGenerator(ProviderType.BLOCK_TAGS, TTags::genBlockTags);
  }
}
