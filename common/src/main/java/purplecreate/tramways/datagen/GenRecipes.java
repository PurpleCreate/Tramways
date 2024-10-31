package purplecreate.tramways.datagen;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import purplecreate.tramways.TBlocks;
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
  }
}
