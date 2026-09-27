package purplecreate.tramways;

import com.simibubi.create.content.decoration.girder.ConnectedGirderModel;
import com.simibubi.create.content.trains.track.TrackTargetingBlockItem;
import com.simibubi.create.foundation.block.DyedBlockList;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import com.simibubi.create.foundation.data.SharedProperties;

import static com.simibubi.create.api.behaviour.display.DisplayTarget.displayTarget;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.axeOnly;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;

import purplecreate.tramways.content.requestStop.station.RequestStopButtonBlock;
import purplecreate.tramways.content.requestStop.station.RequestStopButtonItem;
import purplecreate.tramways.content.signals.TramSignalBlock;
import purplecreate.tramways.content.signs.TramSignBlock;
import purplecreate.tramways.content.announcements.SpeakerBlock;
import purplecreate.tramways.content.stationDeco.nameSign.NameSignBlock;
import purplecreate.tramways.content.stationDeco.nameSign.NameSignItem;
import purplecreate.tramways.datagen.BlockStateBuilders;

public class TBlocks {
  public static final BlockEntry<TramSignalBlock> TRAM_SIGNAL =
    Tramways.REGISTRATE.block("tram_signal", TramSignalBlock::new)
      .initialProperties(SharedProperties::softMetal)
      .properties(properties -> properties.mapColor(MapColor.COLOR_GRAY).sound(SoundType.NETHERITE_BLOCK))
      .blockstate((c, p) ->
        p.horizontalBlock(c.getEntry(), state -> {
          String sigState = state.getValue(TramSignalBlock.STATE).getSerializedName();
          String basePath = "block/" + c.getName();
          return p
            .models()
            .withExistingParent(basePath + "/" + sigState, Tramways.rl(basePath + "/base"))
            .texture("face", Tramways.rl(basePath + "/face_" + sigState));
        })
      )
      .transform(pickaxeOnly())
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
        BlockStateBuilders.simpleHorizontalBlock(context, provider, "block/tram_sign/girder")
      )
      .transform(pickaxeOnly())
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
        BlockStateBuilders.simpleHorizontalBlock(context, provider, "block/tram_sign/girder")
      )
      .transform(pickaxeOnly())
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
        BlockStateBuilders.simpleHorizontalBlock(context, provider, "block/tram_sign/girder")
      )
      .transform(pickaxeOnly())
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
        BlockStateBuilders.simpleDirectionalBlock(context, provider, "block/speaker")
      )
      .transform(axeOnly())
      .transform(displayTarget(TExtras.DisplayTargets.SPEAKER))
      .lang("Speaker")
      .simpleItem()
      .register();

  public static final BlockEntry<RequestStopButtonBlock> REQUEST_STOP_BUTTON =
    Tramways.REGISTRATE.block("request_stop_button", RequestStopButtonBlock::new)
      .initialProperties(SharedProperties::stone)
      .blockstate((context, provider) ->
        BlockStateBuilders.buttonBlock(context, provider, Tramways.rl("block/request_stop_button"))
      )
      .transform(pickaxeOnly())
      .lang("Request Stop Button")
      .item(RequestStopButtonItem::new)
      .transform(customItemModel())
      .register();

  public static final DyedBlockList<NameSignBlock> STATION_NAME_SIGNS = new DyedBlockList<>(color -> {
    String colorId = color.getName();
    String id = colorId + "_station_name_sign";

    return Tramways.REGISTRATE.block(id, NameSignBlock::new)
      .initialProperties(SharedProperties::wooden)
      .blockstate((c, p) ->
        p.horizontalBlock(c.getEntry(), state -> {
          boolean extended = state.getValue(NameSignBlock.EXTENDED);
          String fileTag =  extended ? "_extended" : "";

          return p
            .models()
            .withExistingParent("block/station_name_sign/" + colorId + fileTag, Tramways.rl("block/station_name_sign/base"))
            .texture("texture", Tramways.rl("block/station_name_sign/" + colorId + fileTag));
        })
      )
      .transform(axeOnly())
      .item(NameSignItem::new)
      .model((c, p) ->
        p
          .withExistingParent(c.getName(), Tramways.rl("block/station_name_sign/item_base"))
          .texture("texture", Tramways.rl("block/station_name_sign/" + colorId))
      )
      .tag(TTags.NAME_SIGN)
      .build()
      .register();
  });

  public static void register() {}
}
