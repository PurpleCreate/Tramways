package purplecreate.tramways.datagen;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import purplecreate.tramways.TBlocks;
import purplecreate.tramways.TTags;
import purplecreate.tramways.Tramways;
import purplecreate.tramways.content.stationDeco.nameSign.NameSignBlock;

import static com.tterrag.registrate.providers.RegistrateRecipeProvider.inventoryTrigger;

public class GenRecipes {
  private static InventoryChangeTrigger.TriggerInstance unlockedByItem(ItemLike item) {
    return inventoryTrigger(
      ItemPredicate.Builder
        .item()
        .of(item)
        .build()
    );
  }

  private static InventoryChangeTrigger.TriggerInstance unlockedByItemTag(TagKey<Item> tag) {
    return inventoryTrigger(
      ItemPredicate.Builder
        .item()
        .of(tag)
        .build()
    );
  }

  public static void generator(RegistrateRecipeProvider provider) {
    ShapelessRecipeBuilder
      .shapeless(RecipeCategory.TRANSPORTATION, TBlocks.SPEAKER)
      .unlockedBy("has_item", unlockedByItem(AllBlocks.RAILWAY_CASING))
      .requires(AllBlocks.RAILWAY_CASING)
      .requires(Blocks.NOTE_BLOCK)
      .save(provider);

    ShapedRecipeBuilder
      .shaped(RecipeCategory.TRANSPORTATION, TBlocks.TRAM_SIGN)
      .unlockedBy("has_item", unlockedByItem(AllItems.IRON_SHEET))
      .define('D', Items.BLACK_DYE)
      .define('S', AllItems.IRON_SHEET)
      .define('G', AllBlocks.METAL_GIRDER)
      .pattern("SD")
      .pattern("G ")
      .save(provider);

    ShapelessRecipeBuilder
      .shapeless(RecipeCategory.TRANSPORTATION, TBlocks.RAILWAY_SIGN)
      .unlockedBy("has_item", unlockedByItem(AllItems.IRON_SHEET))
      .requires(TBlocks.TRAM_SIGN)
      .requires(Items.RED_DYE)
      .save(provider);

    ShapedRecipeBuilder
      .shaped(RecipeCategory.TRANSPORTATION, TBlocks.TRAM_SIGNAL)
      .unlockedBy("has_item", unlockedByItem(AllItems.IRON_SHEET))
      .define('E', AllItems.ELECTRON_TUBE)
      .define('S', AllItems.IRON_SHEET)
      .define('G', AllBlocks.METAL_GIRDER)
      .pattern("E")
      .pattern("S")
      .pattern("G")
      .save(provider);

    ShapelessRecipeBuilder
      .shapeless(RecipeCategory.TRANSPORTATION, TBlocks.REQUEST_STOP_BUTTON)
      .unlockedBy("has_item", unlockedByItem(AllItems.BRASS_NUGGET))
      .requires(AllItems.BRASS_NUGGET)
      .requires(Items.STONE_BUTTON)
      .save(provider);

    ShapedRecipeBuilder
      .shaped(RecipeCategory.DECORATIONS, TBlocks.STATION_NAME_SIGNS.get(DyeColor.WHITE))
      .unlockedBy("has_item", unlockedByItemTag(ItemTags.FENCES))
      .define('D', Items.WHITE_DYE)
      .define('P', ItemTags.PLANKS)
      .define('F', ItemTags.FENCES)
      .pattern("PD")
      .pattern("F ")
      .save(provider);

    for (BlockEntry<NameSignBlock> block : TBlocks.STATION_NAME_SIGNS) {
      String name = block.getId().getPath();
      String color = name.substring(0, name.length() - 18);
      ResourceLocation dye = new ResourceLocation(color + "_dye");

      ShapelessRecipeBuilder
        .shapeless(RecipeCategory.DECORATIONS, block)
        .unlockedBy("has_item", unlockedByItem(TBlocks.STATION_NAME_SIGNS.get(DyeColor.WHITE)))
        .requires(TTags.NAME_SIGN)
        .requires(BuiltInRegistries.ITEM.get(dye))
        .save(provider, Tramways.rl(block.getId().getPath() + "__dyed"));
    }
  }
}
