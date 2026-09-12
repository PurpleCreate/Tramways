package purplecreate.tramways.content.signals.item;

import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import net.createmod.catnip.nbt.NBTHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;
import purplecreate.tramways.content.signals.render.SignalType;
import purplecreate.tramways.content.signals.render.SignalTypes;
import purplecreate.tramways.util.IHaveItemRenderer;

public class SignalItem extends BlockItem implements IHaveItemRenderer {
  public SignalItem(Block block, Properties properties) {
    super(block, properties);
  }

  @Environment(EnvType.CLIENT)
  @Nullable
  public static SignalType getSignalType(ItemStack stack) {
    CompoundTag tag = getBlockEntityData(stack);

    return tag != null
      ? SignalTypes.get(NBTHelper.readResourceLocation(tag, "SignalType"))
      : SignalTypes.getDefault();
  }

  @Override
  @Environment(EnvType.CLIENT)
  public CustomRenderedItemModelRenderer getRenderer() {
    return new SignalItemRenderer();
  }
}
