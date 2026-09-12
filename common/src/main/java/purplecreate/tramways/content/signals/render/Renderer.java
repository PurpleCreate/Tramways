package purplecreate.tramways.content.signals.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import purplecreate.tramways.mixinInterfaces.IRGBItemRenderer;

@Environment(EnvType.CLIENT)
public abstract class Renderer {
  public final PoseStack ms;
  public final MultiBufferSource buffer;

  public Renderer(PoseStack ms, MultiBufferSource buffer) {
    this.ms = ms;
    this.buffer = buffer;
  }

  public abstract void renderModel(BakedModel model, RenderType renderType, int light, int overlay, float r, float g, float b);
  public void renderModel(BakedModel model, RenderType renderType, int light, int overlay) {
    renderModel(model, renderType, light, overlay, 1, 1, 1);
  }

  public void renderText(Font font, String text, float x, float y, float scale, int color, int bgColor, int light) {
    ms.pushPose();
    ms.scale(-scale, -scale, scale);
    font.drawInBatch(text, x, y, color, false, ms.last().pose(), buffer, Font.DisplayMode.NORMAL, bgColor, light);
    ms.popPose();
  }

  public static class Block extends Renderer {
    public final BlockEntity be;

    public Block(BlockEntity be, PoseStack ms, MultiBufferSource buffer) {
      super(ms, buffer);
      this.be = be;
    }

    @Override
    public void renderModel(BakedModel model, RenderType renderType, int light, int overlay, float r, float g, float b) {
      Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(
        ms.last(), buffer.getBuffer(renderType), be.getBlockState(),
        model, r, g, b, light, overlay
      );
    }
  }

  public static class Item extends Renderer {
    public final ItemStack stack;
    public final ItemDisplayContext context;

    public Item(ItemStack stack, ItemDisplayContext context, PoseStack ms, MultiBufferSource buffer) {
      super(ms, buffer);
      this.stack = stack;
      this.context = context;
    }

    @Override
    public void renderModel(BakedModel model, RenderType renderType, int light, int overlay, float r, float g, float b) {
      if (Minecraft.getInstance().getItemRenderer() instanceof IRGBItemRenderer renderer) {
        renderer.tramways$render(
          stack, context, ms, buffer.getBuffer(renderType),
          light, overlay, model, (int)(r * 255), (int)(g * 255), (int)(b * 255)
        );
      }
    }
  }
}
