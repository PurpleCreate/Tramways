package purplecreate.tramways.mixins.fabric;

import com.simibubi.create.content.decoration.girder.ConnectedGirderModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import purplecreate.tramways.util.GirderLikeUtil;

import java.util.function.Supplier;

@Mixin(value = ConnectedGirderModel.class, remap = false)
public class ConnectedGirderModelMixin {
  @Inject(
    method = "emitBlockQuads",
    at = @At(
      value = "INVOKE_ASSIGN",
      target = "Lcom/simibubi/create/foundation/block/connected/CTModel;emitBlockQuads(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Ljava/util/function/Supplier;Lnet/fabricmc/fabric/api/renderer/v1/render/RenderContext;)V"
    ),
    cancellable = true
  )
  private void tramways$noBracketsOnGirderLikes(
    BlockAndTintGetter blockView,
    BlockState state,
    BlockPos pos,
    Supplier<RandomSource> randomSupplier,
    RenderContext context,
    CallbackInfo ci
  ) {
    if (GirderLikeUtil.allowed(state, false))
      ci.cancel();
  }
}
