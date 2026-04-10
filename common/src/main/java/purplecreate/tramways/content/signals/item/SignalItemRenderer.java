package purplecreate.tramways.content.signals.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import purplecreate.tramways.content.signals.base.Aspect;
import purplecreate.tramways.content.signals.base.StateHolder;

import java.util.List;

public class SignalItemRenderer extends CustomRenderedItemModelRenderer {
  private List<Aspect<?>> aspects;
  private ItemAnimator animator;

  @Override
  protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer, ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
    if (!(stack.getItem() instanceof SignalItem item)) return;
    renderer.render(model.getOriginalModel(), light);

    if (aspects == null) {
      Aspect.Builder builder = new Aspect.Builder();
      item.getSignalType().addAspects(builder);
      aspects = builder.build();
    }

    if (animator == null) {
      animator = new ItemAnimator();
      item.getSignalType().animateItem(animator);
    }

    float renderTime = AnimationTickHolder.getRenderTime();
    StateHolder stateHolder = animator.getStateForTime(stack, transformType, renderTime);

    ms.pushPose();
    ms.translate(0, 0, -12/16f);

    aspects.forEach(aspect ->
      aspect.render(stateHolder, item.getSignalType(), ms, buffer, renderTime, light, overlay)
    );

    ms.popPose();
  }
}
