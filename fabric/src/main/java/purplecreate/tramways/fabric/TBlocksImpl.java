package purplecreate.tramways.fabric;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.world.level.block.Block;
import purplecreate.tramways.Tramways;
import purplecreate.tramways.content.signals.TramSignalBlock;

public class TBlocksImpl {
  public static <T extends Block> void complexTramSignal(
    DataGenContext<T, ?> context,
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

  public static <T extends Block> void simpleHorizontalBlock(
    DataGenContext<T, ?> context,
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
    DataGenContext<T, ?> context,
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
}
