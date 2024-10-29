package purplecreate.tramways.content.signs;

import com.jozufozu.flywheel.core.PartialModel;
import com.simibubi.create.content.trains.signal.SignalBlockEntity;
import com.simibubi.create.content.trains.track.TrackTargetingBehaviour;
import com.simibubi.create.foundation.render.CachedBufferer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import purplecreate.tramways.content.signs.demands.SignDemand;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import com.simibubi.create.foundation.utility.AngleHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class TramSignRenderer extends SmartBlockEntityRenderer<TramSignBlockEntity> {
  public TramSignRenderer(BlockEntityRendererProvider.Context context) {
    super(context);
  }

  @Override
  protected void renderSafe(TramSignBlockEntity be,
                            float partialTicks,
                            PoseStack ms,
                            MultiBufferSource buffer,
                            int light,
                            int overlay) {
    ms.pushPose();

    ms.pushPose();
    if (be.getOverlay() != SignalBlockEntity.OverlayState.SKIP) {
      BlockPos targetPosition = be.edgePoint.getGlobalPosition();

      TransformStack.cast(ms)
        .translate(targetPosition.subtract(be.getBlockPos()));

      TrackTargetingBehaviour.render(
        be.getLevel(),
        targetPosition,
        be.edgePoint.getTargetDirection(),
        be.edgePoint.getTargetBezier(),
        ms,
        buffer,
        light,
        overlay,
        be.getOverlay() == SignalBlockEntity.OverlayState.DUAL
          ? TrackTargetingBehaviour.RenderedTrackOverlayType.DUAL_SIGNAL
          : TrackTargetingBehaviour.RenderedTrackOverlayType.SIGNAL,
        1
      );
    }
    ms.popPose();

    Direction facing = be
      .getBlockState()
      .getValue(TramSignBlock.FACING);
    SignDemand demand = be.getDemand();
    PartialModel signFace = demand == null
      ? SignDemand.TRAM_FACE
      : demand.getSignFace(be.getSignType());

    CachedBufferer
      .partial(signFace, be.getBlockState())
      .centre()
      .rotateY(AngleHelper.horizontalAngle(
        facing.getOpposite()
      ))
      .unCentre()
      .light(light)
      .renderInto(ms, buffer.getBuffer(RenderType.solid()));

    TransformStack.cast(ms)
      .centre()
      .rotateY(AngleHelper.horizontalAngle(
        facing
      ))
      .unCentre()
      .translate(0, 0, 13.01 / 16f);

    if (demand != null) {
      demand.render(
        be.getSignType(),
        be.getDemandExtra(),
        ms,
        buffer,
        light,
        overlay
      );
    } else {
      SignDemand.renderTextInCenter("?", 0, .8f/16f, ms, buffer, light);
    }

    ms.popPose();
  }
}
