package purplecreate.tramways.fabric;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import io.github.fabricators_of_create.porting_lib.models.generators.ConfiguredModel;
import io.github.fabricators_of_create.porting_lib.models.generators.ModelFile;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.state.properties.AttachFace;
import purplecreate.tramways.Tramways;
import purplecreate.tramways.content.signals.TramSignalBlock;

public class TBlocksImpl {
  public static <T extends Block> void complexTramSignal(
    DataGenContext<Block, T> context,
    RegistrateBlockstateProvider provider
  ) {
    provider.horizontalBlock(context.getEntry(), state -> {
      String sigState = state.getValue(TramSignalBlock.STATE).getSerializedName();
      String basePath = "block/" + context.getName();
      return provider
        .models()
        .withExistingParent(basePath + "/" + sigState, Tramways.rl(basePath + "/base"))
        .texture("face", Tramways.rl(basePath + "/face_" + sigState));
    });
  }

  public static <T extends Block> void complexStationNameSign(
    DataGenContext<Block, T> context,
    RegistrateBlockstateProvider provider,
    String color
  ) {
    provider.horizontalBlock(context.getEntry(), state ->
      provider
        .models()
        .withExistingParent("block/station_name_sign/" + color, Tramways.rl("block/station_name_sign/base"))
        .texture("texture", Tramways.rl("block/station_name_sign/" + color))
    );
  }

  public static <T extends Item> void complexStationNameSignItem(
    DataGenContext<Item, T> context,
    RegistrateItemModelProvider provider,
    String color
  ) {
    provider
      .withExistingParent(context.getName(), Tramways.rl("block/station_name_sign/item_base"))
      .texture("texture", Tramways.rl("block/station_name_sign/" + color));
  }

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
