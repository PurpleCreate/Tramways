package purplecreate.tramways.mixins;

import com.simibubi.create.content.trains.station.StationBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
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
  @Inject(method = "use", at = @At("HEAD"), cancellable = true)
  private void tramways$cancelScreenOpening(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit, CallbackInfoReturnable<InteractionResult> cir) {
    if (TBlocks.REQUEST_STOP_BUTTON.isIn(pPlayer.getItemInHand(pHand))) {
      cir.setReturnValue(InteractionResult.PASS);
    }
  }
}
