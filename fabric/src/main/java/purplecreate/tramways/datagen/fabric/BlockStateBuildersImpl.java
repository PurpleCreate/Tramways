package purplecreate.tramways.datagen.fabric;

import io.github.fabricators_of_create.porting_lib.models.generators.ConfiguredModel;
import io.github.fabricators_of_create.porting_lib.models.generators.ModelFile;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.state.properties.AttachFace;
import purplecreate.tramways.Tramways;
import purplecreate.tramways.content.signals.block.SignAttachedToPoleBlock;
import purplecreate.tramways.content.stationDeco.nameSign.NameSignBlock;
import purplecreate.tramways.datagen.BlockStateBuilders.BlockConsumer;
import purplecreate.tramways.datagen.BlockStateBuilders.ItemConsumer;

public class BlockStateBuildersImpl {
  public static <T extends SignAttachedToPoleBlock> BlockConsumer<T> empty() {
    return (context, provider) -> {
      provider.simpleBlock(
        context.getEntry(),
        provider.models().getExistingFile(provider.mcLoc("block/block"))
      );
    };
  }

  public static <T extends Block> BlockConsumer<T> stationNameSign(String color) {
    return (context, provider) -> {
      provider.horizontalBlock(context.getEntry(), state -> {
        boolean extended = state.getValue(NameSignBlock.EXTENDED);
        String fileTag =  extended ? "_extended" : "";

        return provider
          .models()
          .withExistingParent("block/station_name_sign/" + color + fileTag, Tramways.rl("block/station_name_sign/base"))
          .texture("texture", Tramways.rl("block/station_name_sign/" + color + fileTag));
      });
    };
  }

  public static <T extends Item> ItemConsumer<T> stationNameSignItem(String color) {
    return (context, provider) -> {
      provider
        .withExistingParent(context.getName(), Tramways.rl("block/station_name_sign/item_base"))
        .texture("texture", Tramways.rl("block/station_name_sign/" + color));
    };
  }

  public static <T extends Block> BlockConsumer<T> horizontalBlock(String existingModelPath) {
    return (context, provider) -> {
      provider.horizontalBlock(
        context.getEntry(),
        provider
          .models()
          .getExistingFile(
            provider.modLoc(existingModelPath)
          )
      );
    };
  }

  public static <T extends Block> BlockConsumer<T> directionalBlock(String existingModelPath) {
    return (context, provider) -> {
      provider.directionalBlock(
        context.getEntry(),
        provider
          .models()
          .getExistingFile(
            provider.modLoc(existingModelPath)
          )
      );
    };
  }

  public static <T extends ButtonBlock> BlockConsumer<T> buttonBlock(ResourceLocation texture) {
    return (context, provider) -> {
      ModelFile button = provider
        .models()
        .withExistingParent(
          "block/" + context.getName() + "/normal",
          provider.mcLoc("block/button")
        )
        .texture("texture", texture);
      ModelFile buttonPressed = provider
        .models()
        .withExistingParent(
          "block/" + context.getName() + "/pressed",
          provider.mcLoc("block/button_pressed")
        )
        .texture("texture", texture);

      provider.getVariantBuilder(context.getEntry()).forAllStates((state) -> {
        Direction facing = state.getValue(ButtonBlock.FACING);
        AttachFace face = state.getValue(ButtonBlock.FACE);
        boolean powered = state.getValue(ButtonBlock.POWERED);
        return ConfiguredModel.builder()
          .modelFile(powered ? buttonPressed : button)
          .rotationX(face == AttachFace.FLOOR ? 0 : (face == AttachFace.WALL ? 90 : 180))
          .rotationY((int)(face == AttachFace.CEILING ? facing : facing.getOpposite()).toYRot())
          .build();
      });
    };
  }
}
