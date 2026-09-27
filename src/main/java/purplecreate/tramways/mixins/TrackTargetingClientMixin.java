package purplecreate.tramways.mixins;

import dev.engine_room.flywheel.lib.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.trains.graph.EdgePointType;
import com.simibubi.create.content.trains.graph.TrackGraphLocation;
import com.simibubi.create.content.trains.track.BezierTrackPointLocation;
import com.simibubi.create.content.trains.track.TrackTargetingBehaviour;
import com.simibubi.create.content.trains.track.TrackTargetingBlockItem;
import com.simibubi.create.content.trains.track.TrackTargetingClient;
import net.createmod.catnip.render.SuperRenderTypeBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import purplecreate.tramways.TExtras;

@Mixin(value = TrackTargetingClient.class, remap = false)
public class TrackTargetingClientMixin {
  @Shadow private static BlockPos lastHovered;
  @Shadow private static boolean lastDirection;
  @Shadow private static BezierTrackPointLocation lastHoveredBezierSegment;
  @Shadow private static EdgePointType<?> lastType;
  @Shadow private static TrackGraphLocation lastLocation;
  @Shadow private static TrackTargetingBlockItem.OverlapResult lastResult;

  @Inject(method = "render", at = @At("HEAD"), cancellable = true)
  private static void tramways$renderTramSignIcon(PoseStack ms, SuperRenderTypeBuffer buffer, Vec3 camera, CallbackInfo ci) {
    if (
      lastLocation != null
        && lastResult.feedback == null
        && lastType == TExtras.EdgePointTypes.TRAM_SIGN
    ) {
      Minecraft mc = Minecraft.getInstance();
      BlockPos pos = lastHovered;

      ms.pushPose();
      TransformStack.of(ms)
        .translate(Vec3.atLowerCornerOf(pos).subtract(camera));
      TrackTargetingBehaviour.render(
        mc.level,
        lastHovered,
        lastDirection ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE,
        lastHoveredBezierSegment,
        ms,
        buffer,
        LevelRenderer.getLightColor(mc.level, lastHovered),
        OverlayTexture.NO_OVERLAY,
        TrackTargetingBehaviour.RenderedTrackOverlayType.SIGNAL,
        1 + 1 / 16f
      );
      ms.popPose();

      ci.cancel();
    }
  }
}
