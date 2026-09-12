package purplecreate.tramways.content.signals.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.Direction;
import purplecreate.tramways.content.signals.base.Aspect;
import purplecreate.tramways.content.signals.base.StateHolder;

public class SignalRenderer extends SmartBlockEntityRenderer<SignalBlockEntity> {
  private final SignalType signalType;

  private SignalRenderer(Context context, SignalType signalType) {
    super(context);
    this.signalType = signalType;
  }

  public static NonNullFunction<Context, BlockEntityRenderer<? super SignalBlockEntity>> of(SignalType signalType) {
    return (context) -> new SignalRenderer(context, signalType);
  }

  @Override
  protected void renderSafe(SignalBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
    if (!(be.getBlockState().getBlock() instanceof SignalBlock block)) return;

    super.renderSafe(be, partialTicks, ms, buffer, light, overlay);
    if (be.aspects == null) {
      Aspect.Builder builder = new Aspect.Builder();
      signalType.addAspects(builder);
      be.aspects = builder.build();
    }

    var msr = TransformStack.of(ms);
    float renderTime = AnimationTickHolder.getRenderTime(be.getLevel());
    Direction facing = be.getBlockState().getValue(SignalBlock.FACING);
    StateHolder stateHolder = StateHolder.forBlockEntity(be);

    msr.translate(block.getRenderOffset(be.getBlockState()));
    msr.center().rotateYDegrees(AngleHelper.horizontalAngle(facing.getOpposite())).uncenter();

    be.aspects.forEach(aspect ->
      aspect.render(stateHolder, signalType, ms, buffer, renderTime, light, overlay)
    );
  }
}
