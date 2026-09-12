package purplecreate.tramways.content.signals.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.resources.model.BakedModel;
import purplecreate.tramways.content.signals.render.Renderer;
import purplecreate.tramways.content.signals.render.SignalType;

public class GenericSignalRenderer extends SmartBlockEntityRenderer<GenericSignalBlockEntity> {
  public GenericSignalRenderer(Context context) {
    super(context);
  }

  @Override
  public int getViewDistance() {
    return Minecraft.getInstance().options.getEffectiveRenderDistance() * 16;
  }

  @Override
  protected void renderSafe(GenericSignalBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
    SignalType signalType = be.getSignalType();

    if (signalType == null) {
      Minecraft mc = Minecraft.getInstance();
      BakedModel missingModel = mc.getModelManager().getMissingModel();
      mc.getBlockRenderer().getModelRenderer().renderModel(
        ms.last(),
        buffer.getBuffer(RenderType.solid()),
        be.getBlockState(),
        missingModel,
        1,
        1,
        1,
        light,
        overlay
      );
      return;
    }

    signalType.render(new Renderer.Block(be, ms, buffer), partialTicks, light, overlay);
  }
}
