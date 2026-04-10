package purplecreate.tramways;

import com.simibubi.create.content.trains.track.TrackTargetingBlockItem;
import com.simibubi.create.foundation.block.DyedBlockList;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import com.simibubi.create.foundation.data.SharedProperties;
import purplecreate.tramways.content.signals.block.SignAttachedToPoleBlock;
import purplecreate.tramways.content.signals.models.SignAttachedToPoleModel;
import purplecreate.tramways.datagen.BlockStateBuilders;

import static com.simibubi.create.api.behaviour.display.DisplayTarget.displayTarget;
import static com.simibubi.create.api.behaviour.movement.MovementBehaviour.movementBehaviour;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.axeOnly;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;

import purplecreate.tramways.content.announcements.SpeakerMovementBehaviour;
import purplecreate.tramways.content.requestStop.station.RequestStopButtonBlock;
import purplecreate.tramways.content.requestStop.station.RequestStopButtonItem;
import purplecreate.tramways.content.signs.TramSignBlock;
import purplecreate.tramways.content.announcements.SpeakerBlock;
import purplecreate.tramways.content.stationDeco.nameSign.NameSignBlock;
import purplecreate.tramways.content.stationDeco.nameSign.NameSignItem;

public class TBlocks {
  public static final BlockEntry<TramSignBlock> TRAM_SIGN =
    Tramways.REGISTRATE.block("tram_sign", TramSignBlock::newTramSign)
      .initialProperties(SharedProperties::softMetal)
      .properties(properties -> properties.mapColor(MapColor.COLOR_GRAY).sound(SoundType.NETHERITE_BLOCK))
      .blockstate(BlockStateBuilders.empty())
      .tag(TTags.pole())
      .transform(pickaxeOnly())
      .onRegister(attachedBlock())
      .lang("Tram Sign")
      .item(TrackTargetingBlockItem.ofType(TExtras.EdgePointTypes.TRAM_SIGN))
      .transform(customItemModel())
      .register();

  public static final BlockEntry<TramSignBlock> RAILWAY_SIGN =
    Tramways.REGISTRATE.block("railway_sign", TramSignBlock::newRailwaySign)
      .initialProperties(SharedProperties::softMetal)
      .properties(properties -> properties.mapColor(MapColor.COLOR_GRAY).sound(SoundType.NETHERITE_BLOCK))
      .blockstate(BlockStateBuilders.empty())
      .tag(TTags.pole())
      .transform(pickaxeOnly())
      .onRegister(attachedBlock())
      .lang("Railway Sign")
      .item(TrackTargetingBlockItem.ofType(TExtras.EdgePointTypes.TRAM_SIGN))
      .transform(customItemModel())
      .register();

  public static final BlockEntry<TramSignBlock> AUXILIARY_SIGN =
    Tramways.REGISTRATE.block("auxiliary_sign", TramSignBlock::newAuxiliarySign)
      .initialProperties(SharedProperties::softMetal)
      .properties(properties -> properties.mapColor(MapColor.COLOR_GRAY).sound(SoundType.NETHERITE_BLOCK))
      .blockstate(BlockStateBuilders.empty())
      .tag(TTags.pole())
      .transform(pickaxeOnly())
      .onRegister(attachedBlock())
      .lang("Auxiliary Rail Sign")
      .item()
      .transform(customItemModel())
      .register();

  public static final BlockEntry<SpeakerBlock> SPEAKER =
    Tramways.REGISTRATE.block("speaker", SpeakerBlock::new)
      .initialProperties(SharedProperties::wooden)
      .properties(properties -> properties.mapColor(MapColor.COLOR_BROWN).sound(SoundType.WOOD))
      .blockstate(BlockStateBuilders.directionalBlock("block/speaker"))
      .transform(axeOnly())
      .transform(displayTarget(TExtras.DisplayTargets.SPEAKER))
      .onRegister(movementBehaviour(new SpeakerMovementBehaviour()))
      .lang("Speaker")
      .simpleItem()
      .register();

  public static final BlockEntry<RequestStopButtonBlock> REQUEST_STOP_BUTTON =
    Tramways.REGISTRATE.block("request_stop_button", RequestStopButtonBlock::new)
      .initialProperties(SharedProperties::stone)
      .blockstate(BlockStateBuilders.buttonBlock(Tramways.rl("block/request_stop_button")))
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
      .blockstate(BlockStateBuilders.stationNameSign(colorId))
      .transform(axeOnly())
      .item(NameSignItem::new)
      .model(BlockStateBuilders.stationNameSignItem(colorId))
      .tag(TTags.NAME_SIGN)
      .build()
      .register();
  });

  public static NonNullConsumer<? super SignAttachedToPoleBlock> attachedBlock() {
    return CreateRegistrate.blockModel(() -> SignAttachedToPoleModel::create);
  }

  public static void register() {}
}
