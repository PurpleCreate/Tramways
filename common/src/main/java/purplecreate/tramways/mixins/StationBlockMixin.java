package purplecreate.tramways.mixins;

import com.simibubi.create.content.trains.station.StationBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import purplecreate.tramways.TBlocks;

@Mixin(value = StationBlock.class, remap = false)
public class StationBlockMixin {
  @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true, remap = true)
  private void tramways$cancelScreenOpening(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<ItemInteractionResult> cir) {
    if (TBlocks.REQUEST_STOP_BUTTON.isIn(stack)) {
      cir.setReturnValue(ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION);
    }
  }
}
