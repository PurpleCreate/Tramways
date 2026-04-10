package purplecreate.tramways;

import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import purplecreate.tramways.content.signals.block.SignalBlock;
import purplecreate.tramways.content.signals.types.FourAspectSignalType;
import purplecreate.tramways.content.signals.types.TheatreSignalType;
import purplecreate.tramways.content.signals.types.TramSignalType;
import purplecreate.tramways.datagen.BlockStateBuilders;

import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;

public class TSignalTypes {
  public static final BlockEntry<SignalBlock> FOUR_ASPECT_SIGNAL =
    Tramways.REGISTRATE.signalType("four_aspect_signal", FourAspectSignalType::new)
      .attachesToBlock()
      .bindsToSignal()
      .cycleCategory("uk_mainline")
      .initialProperties(SharedProperties::softMetal)
      .properties(properties -> properties.mapColor(MapColor.COLOR_GRAY).sound(SoundType.NETHERITE_BLOCK))
      .blockstate(BlockStateBuilders.horizontalBlock("block/signals/four_aspect_signal"))
      .addLayer(() -> RenderType::cutout)
      .transform(pickaxeOnly())
      .lang("Four Aspect Signal")
      .register();

  public static final BlockEntry<SignalBlock> THEATRE_SIGNAL =
    Tramways.REGISTRATE.signalType("theatre_signal", TheatreSignalType::new)
      .attachesToBlock()
      .bindsToSignal()
      .cycleCategory("uk_mainline")
      .initialProperties(SharedProperties::softMetal)
      .properties(properties -> properties.mapColor(MapColor.COLOR_GRAY).sound(SoundType.NETHERITE_BLOCK))
      .blockstate(BlockStateBuilders.horizontalBlock("block/signals/tram_signal"))
      .transform(pickaxeOnly())
      .lang("Theatre Signal")
      .register();

  public static final BlockEntry<SignalBlock> TRAM_SIGNAL =
    Tramways.REGISTRATE.signalType("tram_signal", TramSignalType::new)
      .attachesToBlock()
      .bindsToSignal()
      .cycleCategory("uk_tramway")
      .initialProperties(SharedProperties::softMetal)
      .properties(properties -> properties.mapColor(MapColor.COLOR_GRAY).sound(SoundType.NETHERITE_BLOCK))
      .blockstate(BlockStateBuilders.horizontalBlock("block/signals/tram_signal"))
      .transform(pickaxeOnly())
      .lang("Tram Signal")
      .register();

  public static void register() {}
}
