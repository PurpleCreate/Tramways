package purplecreate.tramways;

import com.simibubi.create.content.decoration.girder.ConnectedGirderModel;
import com.simibubi.create.content.trains.track.TrackTargetingBlockItem;
import com.simibubi.create.foundation.block.DyedBlockList;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import com.tterrag.registrate.util.entry.BlockEntry;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import com.simibubi.create.foundation.data.SharedProperties;

import static com.simibubi.create.AllMovementBehaviours.movementBehaviour;
import static com.simibubi.create.content.redstone.displayLink.AllDisplayBehaviours.assignDataBehaviour;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;

import purplecreate.tramways.content.announcements.SpeakerDisplayTarget;
import purplecreate.tramways.content.announcements.SpeakerMovementBehaviour;
import purplecreate.tramways.content.requestStop.station.RequestStopButtonBlock;
import purplecreate.tramways.content.requestStop.station.RequestStopButtonItem;
import purplecreate.tramways.content.signals.TramSignalBlock;
import purplecreate.tramways.content.signs.TramSignBlock;
import purplecreate.tramways.content.announcements.SpeakerBlock;
import purplecreate.tramways.content.stationDeco.nameSign.NameSignBlock;
import purplecreate.tramways.content.stationDeco.nameSign.NameSignItem;

public class TBlocks {
  public static final BlockEntry<TramSignalBlock> TRAM_SIGNAL =
    Tramways.REGISTRATE.block("tram_signal", TramSignalBlock::new)
      .initialProperties(SharedProperties::softMetal)
      .properties(properties -> properties.mapColor(MapColor.COLOR_GRAY).sound(SoundType.NETHERITE_BLOCK))
      .blockstate(TBlocks::complexTramSignal)
      .onRegister(CreateRegistrate.blockModel(() -> ConnectedGirderModel::new))
      .lang("Tram Signal")
      .item()
      .transform(customItemModel())
      .register();

  public static final BlockEntry<TramSignBlock> TRAM_SIGN =
    Tramways.REGISTRATE.block("tram_sign", TramSignBlock::newTramSign)
      .initialProperties(SharedProperties::softMetal)
      .properties(properties -> properties.mapColor(MapColor.COLOR_GRAY).sound(SoundType.NETHERITE_BLOCK))
      .blockstate((context, provider) ->
        simpleHorizontalBlock(context, provider, "block/tram_sign/girder")
      )
      .onRegister(CreateRegistrate.blockModel(() -> ConnectedGirderModel::new))
      .lang("Tram Sign")
      .item(TrackTargetingBlockItem.ofType(TExtras.EdgePointTypes.TRAM_SIGN))
      .transform(customItemModel())
      .register();

  public static final BlockEntry<TramSignBlock> RAILWAY_SIGN =
    Tramways.REGISTRATE.block("railway_sign", TramSignBlock::newRailwaySign)
      .initialProperties(SharedProperties::softMetal)
      .properties(properties -> properties.mapColor(MapColor.COLOR_GRAY).sound(SoundType.NETHERITE_BLOCK))
      .blockstate((context, provider) ->
        simpleHorizontalBlock(context, provider, "block/tram_sign/girder")
      )
      .onRegister(CreateRegistrate.blockModel(() -> ConnectedGirderModel::new))
      .lang("Railway Sign")
      .item(TrackTargetingBlockItem.ofType(TExtras.EdgePointTypes.TRAM_SIGN))
      .transform(customItemModel())
      .register();

  public static final BlockEntry<TramSignBlock> AUXILIARY_SIGN =
    Tramways.REGISTRATE.block("auxiliary_sign", TramSignBlock::newAuxiliarySign)
      .initialProperties(SharedProperties::softMetal)
      .properties(properties -> properties.mapColor(MapColor.COLOR_GRAY).sound(SoundType.NETHERITE_BLOCK))
      .blockstate((context, provider) ->
        simpleHorizontalBlock(context, provider, "block/tram_sign/girder")
      )
      .onRegister(CreateRegistrate.blockModel(() -> ConnectedGirderModel::new))
      .lang("Auxiliary Rail Sign")
      .item()
      .transform(customItemModel())
      .register();

  public static final BlockEntry<SpeakerBlock> SPEAKER =
    Tramways.REGISTRATE.block("speaker", SpeakerBlock::new)
      .initialProperties(SharedProperties::wooden)
      .properties(properties -> properties.mapColor(MapColor.COLOR_BROWN).sound(SoundType.WOOD))
      .blockstate((context, provider) ->
        simpleDirectionalBlock(context, provider, "block/speaker")
      )
      .onRegister(assignDataBehaviour(new SpeakerDisplayTarget()))
      .onRegister(movementBehaviour(new SpeakerMovementBehaviour()))
      .lang("Speaker")
      .simpleItem()
      .register();

  public static final BlockEntry<RequestStopButtonBlock> REQUEST_STOP_BUTTON =
    Tramways.REGISTRATE.block("request_stop_button", RequestStopButtonBlock::new)
      .initialProperties(SharedProperties::stone)
      .blockstate((context, provider) ->
        buttonBlock(context, provider, Tramways.rl("block/request_stop_button"))
      )
      .lang("Request Stop Button")
      .item(RequestStopButtonItem::new)
      .transform(customItemModel())
      .register();

  public static final DyedBlockList<NameSignBlock> STATION_NAME_SIGNS = new DyedBlockList<>(color -> {
    String colorId = color.getName();
    String id = colorId + "_station_name_sign";

    return Tramways.REGISTRATE.block(id, NameSignBlock::new)
      .initialProperties(SharedProperties::wooden)
      .blockstate((context, provider) ->
        complexStationNameSign(context, provider, colorId)
      )
      .item(NameSignItem::new)
      .model((context, provider) ->
        complexStationNameSignItem(context, provider, colorId)
      )
      .tag(TTags.NAME_SIGN)
      .build()
      .register();
  });

  @ExpectPlatform
  public static <T extends Block> void complexTramSignal(
    DataGenContext<Block, T> context,
    RegistrateBlockstateProvider provider
  ) {
    throw new AssertionError();
  }

  @ExpectPlatform
  public static <T extends Block> void complexStationNameSign(
    DataGenContext<Block, T> context,
    RegistrateBlockstateProvider provider,
    String color
  ) {
    throw new AssertionError();
  }

  @ExpectPlatform
  public static <T extends Item> void complexStationNameSignItem(
    DataGenContext<Item, T> context,
    RegistrateItemModelProvider provider,
    String color
  ) {
    throw new AssertionError();
  }

  @ExpectPlatform
  public static <T extends Block> void simpleHorizontalBlock(
    DataGenContext<Block, T> context,
    RegistrateBlockstateProvider provider,
    String existingModelPath
  ) {
    throw new AssertionError();
  }

  @ExpectPlatform
  public static <T extends Block> void simpleDirectionalBlock(
    DataGenContext<Block, T> context,
    RegistrateBlockstateProvider provider,
    String existingModelPath
  ) {
    throw new AssertionError();
  }

  @ExpectPlatform
  public static <T extends ButtonBlock> void buttonBlock(
    DataGenContext<Block, T> context,
    RegistrateBlockstateProvider provider,
    ResourceLocation texture
  ) {
    throw new AssertionError();
  }

  public static void register() {}
}
