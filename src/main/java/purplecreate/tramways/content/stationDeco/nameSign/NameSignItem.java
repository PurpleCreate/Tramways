package purplecreate.tramways.content.stationDeco.nameSign;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class NameSignItem extends BlockItem {
  public NameSignItem(Block block, Properties properties) {
    super(block, properties);
  }

  @Override
  protected boolean updateCustomBlockEntityTag(BlockPos pos, Level level, @Nullable Player player, ItemStack stack, BlockState state) {
    if (
      level.isClientSide
        && player != null
        && state.getBlock() instanceof NameSignBlock block
    ) {
      block.openScreen(level, pos);
    }

    return super.updateCustomBlockEntityTag(pos, level, player, stack, state);
  }
}
