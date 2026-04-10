package purplecreate.tramways.mixins.forge;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import purplecreate.tramways.mixinInterfaces.IRGBItemRenderer;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin implements IRGBItemRenderer {
  @Shadow protected abstract void renderModelLists(BakedModel model, ItemStack stack, int combinedLight, int combinedOverlay, PoseStack poseStack, VertexConsumer buffer);

  @Unique private boolean tramways$shouldColor = false;
  @Unique private int tramways$r = 0;
  @Unique private int tramways$g = 0;
  @Unique private int tramways$b = 0;

  @Override
  public void tramways$render(ItemStack itemStack, ItemDisplayContext context, PoseStack poseStack, VertexConsumer buffer, int combinedLight, int combinedOverlay, BakedModel model, int red, int green, int blue) {
    tramways$shouldColor = true;
    tramways$r = red;
    tramways$g = green;
    tramways$b = blue;
    tramways$setupRender(itemStack, context, poseStack, buffer, combinedLight, combinedOverlay, model);
    renderModelLists(model, itemStack, combinedLight, combinedOverlay, poseStack, buffer);
    tramways$shouldColor = false;
  }

  @WrapOperation(method = "renderQuadList", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;putBulkData(Lcom/mojang/blaze3d/vertex/PoseStack$Pose;Lnet/minecraft/client/renderer/block/model/BakedQuad;FFFFIIZ)V"))
  private void tramways$renderImpl(VertexConsumer instance, PoseStack.Pose pose, BakedQuad quad, float red, float green, float blue, float v, int combinedLight, int combinedOverlay, boolean b, Operation<Void> original) {
    if (tramways$shouldColor) {
      red = tramways$r / 255f;
      green = tramways$g / 255f;
      blue = tramways$b / 255f;
    }

    original.call(instance, pose, quad, red, green, blue, v, combinedLight, combinedOverlay, b);
  }
}
