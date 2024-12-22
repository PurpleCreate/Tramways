package purplecreate.tramways.mixins.forge;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.decoration.girder.ConnectedGirderModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import purplecreate.tramways.util.GirderLikeUtil;

import java.util.List;

@Mixin(value = ConnectedGirderModel.class, remap = false)
public class ConnectedGirderModelMixin {
  @Inject(
    method = "getQuads",
    at = @At(
      value = "INVOKE_ASSIGN",
      target = "Lcom/simibubi/create/foundation/block/connected/CTModel;getQuads(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/util/RandomSource;Lnet/minecraftforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)Ljava/util/List;"
    ),
    cancellable = true
  )
  private void tramways$noBracketsOnGirderLikes(
    BlockState state,
    Direction side,
    RandomSource rand,
    ModelData extraData,
    RenderType renderType,
    CallbackInfoReturnable<List<BakedQuad>> cir,
    @Local List<BakedQuad> superQuads
  ) {
    if (GirderLikeUtil.allowed(state, false))
      cir.setReturnValue(superQuads);
  }
}
