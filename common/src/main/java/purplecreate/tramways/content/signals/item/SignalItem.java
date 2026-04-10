package purplecreate.tramways.content.signals.item;

import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.tterrag.registrate.util.nullness.NonNullBiFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.level.block.Block;
import purplecreate.tramways.content.signals.base.SignalType;
import purplecreate.tramways.util.IHaveItemRenderer;

public class SignalItem extends CyclableItem implements IHaveItemRenderer {
  private final SignalType signalType;

  protected SignalItem(Block block, Properties properties, SignalType signalType, String category) {
    super(block, properties, CycleRegistries.SIGNALS, category);
    this.signalType = signalType;
  }

  public static NonNullBiFunction<Block, Properties, SignalItem> of(SignalType signalType, String category) {
    return (block, properties) -> new SignalItem(block, properties, signalType, category);
  }

  public SignalType getSignalType() {
    return signalType;
  }

  @Override
  @Environment(EnvType.CLIENT)
  public CustomRenderedItemModelRenderer getRenderer() {
    return new SignalItemRenderer();
  }
}
