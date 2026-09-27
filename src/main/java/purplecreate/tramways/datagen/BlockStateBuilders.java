package purplecreate.tramways.datagen;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;

public class BlockStateBuilders {
  public static <T extends Block> void simpleHorizontalBlock(
    DataGenContext<Block, T> context,
    RegistrateBlockstateProvider provider,
    String existingModelPath
  ) {
    provider.horizontalBlock(
      context.getEntry(),
      provider
        .models()
        .getExistingFile(
          provider.modLoc(existingModelPath)
        )
    );
  }

  public static <T extends Block> void simpleDirectionalBlock(
    DataGenContext<Block, T> context,
    RegistrateBlockstateProvider provider,
    String existingModelPath
  ) {
    provider.directionalBlock(
      context.getEntry(),
      provider
        .models()
        .getExistingFile(
          provider.modLoc(existingModelPath)
        )
    );
  }

  public static <T extends ButtonBlock> void buttonBlock(
    DataGenContext<Block, T> context,
    RegistrateBlockstateProvider provider,
    ResourceLocation texture
  ) {
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
  }
}
