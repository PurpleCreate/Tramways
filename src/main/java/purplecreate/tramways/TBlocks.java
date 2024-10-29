package purplecreate.tramways;

import com.simibubi.create.content.trains.track.TrackTargetingBlockItem;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import com.simibubi.create.foundation.data.SharedProperties;
import net.minecraftforge.client.model.generators.ModelFile;

import static com.simibubi.create.AllMovementBehaviours.movementBehaviour;
import static com.simibubi.create.content.redstone.displayLink.AllDisplayBehaviours.assignDataBehaviour;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;

import purplecreate.tramways.content.announcements.SpeakerDisplayTarget;
import purplecreate.tramways.content.announcements.SpeakerMovementBehaviour;
import purplecreate.tramways.content.signals.TramSignalBlock;
import purplecreate.tramways.content.signs.TramSignBlock;
import purplecreate.tramways.content.announcements.SpeakerBlock;

public class TBlocks {
  public static final BlockEntry<TramSignalBlock> TRAM_SIGNAL =
    Tramways.REGISTRATE.block("tram_signal", TramSignalBlock::new)
      .initialProperties(SharedProperties::softMetal)
      .properties(properties -> properties.mapColor(MapColor.COLOR_GRAY).sound(SoundType.NETHERITE_BLOCK))
      .blockstate((context, provider) ->
        provider.horizontalBlock(context.getEntry(), state -> {
          String sigState = state.getValue(TramSignalBlock.STATE).getSerializedName();
          String basePath = "block/" + context.getName();
          return provider
            .models()
            .withExistingParent(basePath + "/" + sigState, Tramways.rl(basePath + "/base"))
            .texture("face", Tramways.rl(basePath + "/face_" + sigState));
        })
      )
      .lang("Tram Signal")
      .item()
      .transform(customItemModel())
      .register();

  public static final BlockEntry<TramSignBlock> TRAM_SIGN =
    Tramways.REGISTRATE.block("tram_sign", TramSignBlock::newTramSign)
      .initialProperties(SharedProperties::softMetal)
      .properties(properties -> properties.mapColor(MapColor.COLOR_GRAY).sound(SoundType.NETHERITE_BLOCK))
      .blockstate((context, provider) ->
        provider.horizontalBlock(
          context.getEntry(),
          provider
            .models()
            .getExistingFile(
              provider.modLoc("block/tram_sign/girder")
            )
        )
      )
      .lang("Tram Sign")
      .item(TrackTargetingBlockItem.ofType(TExtras.EdgePointTypes.TRAM_SIGN))
      .transform(customItemModel())
      .register();

  public static final BlockEntry<TramSignBlock> RAILWAY_SIGN =
    Tramways.REGISTRATE.block("railway_sign", TramSignBlock::newRailwaySign)
      .initialProperties(SharedProperties::softMetal)
      .properties(properties -> properties.mapColor(MapColor.COLOR_GRAY).sound(SoundType.NETHERITE_BLOCK))
      .blockstate((context, provider) ->
        provider.horizontalBlock(
          context.getEntry(),
          provider
            .models()
            .getExistingFile(
              provider.modLoc("block/tram_sign/girder")
            )
        )
      )
      .lang("Railway Sign")
      .item(TrackTargetingBlockItem.ofType(TExtras.EdgePointTypes.TRAM_SIGN))
      .transform(customItemModel())
      .register();

  public static final BlockEntry<SpeakerBlock> SPEAKER =
    Tramways.REGISTRATE.block("speaker", SpeakerBlock::new)
      .initialProperties(SharedProperties::wooden)
      .properties(properties -> properties.mapColor(MapColor.COLOR_BROWN).sound(SoundType.WOOD))
      .blockstate((context, provider) ->
        provider.directionalBlock(
          context.getEntry(),
          simpleBlockModel(context, provider)
        )
      )
      .onRegister(assignDataBehaviour(new SpeakerDisplayTarget()))
      .onRegister(movementBehaviour(new SpeakerMovementBehaviour()))
      .lang("Speaker")
      .simpleItem()
      .register();

  private static ModelFile simpleBlockModel(DataGenContext<?, ?> context, RegistrateBlockstateProvider provider) {
    return provider.models().getExistingFile(provider.modLoc("block/" + context.getName()));
  }

  public static void register() {}
}
