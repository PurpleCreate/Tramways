package purplecreate.tramways.content.signals.item;

import com.tterrag.registrate.util.nullness.NonNullBiFunction;
import net.createmod.catnip.gui.ScreenOpener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import purplecreate.tramways.content.signals.item.CycleRegistries.CycleRegistry;
import purplecreate.tramways.util.Env;

public class CyclableItem extends BlockItem {
  private final CycleRegistry registry;
  private final String category;

  protected CyclableItem(Block block, Properties properties, CycleRegistry registry, String category) {
    super(block, properties);
    this.registry = registry;
    this.category = category;
  }

  public static NonNullBiFunction<Block, Properties, CyclableItem> of(CycleRegistry registry, String category) {
    return (block, properties) -> new CyclableItem(block, properties, registry, category);
  }

  public void onAfterRegister() {
    registry.register(category, this);
  }

  @Override
  public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
    if (!player.isShiftKeyDown()) return super.use(level, player, hand);

    ItemStack heldItem = player.getItemInHand(hand);
    Env.unsafeRunWhenOn(Env.CLIENT, () -> this::openCycleScreen);
    return InteractionResultHolder.success(heldItem);
  }
  
  @Environment(EnvType.CLIENT)
  private void openCycleScreen() {
    ScreenOpener.open(new ItemCycleScreen(this, registry.get()));
  }
}
