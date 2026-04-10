package purplecreate.tramways.mixinInterfaces;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public interface IRGBItemRenderer {
  void tramways$render(ItemStack itemStack, ItemDisplayContext context, PoseStack poseStack, VertexConsumer buffer, int combinedLight, int combinedOverlay, BakedModel model, int red, int green, int blue);

  default void tramways$setupRender(ItemStack itemStack, ItemDisplayContext context, PoseStack poseStack, VertexConsumer buffer, int combinedLight, int combinedOverlay, BakedModel model) {
    boolean leftHand = context == ItemDisplayContext.FIRST_PERSON_LEFT_HAND
      || context == ItemDisplayContext.THIRD_PERSON_LEFT_HAND;

    model.getTransforms().getTransform(context).apply(leftHand, poseStack);
    poseStack.translate(-0.5F, -0.5F, -0.5F);
  }
}
