package purplecreate.tramways.content.signals.base;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.createmod.catnip.render.SuperBufferFactory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import purplecreate.tramways.content.signals.block.SignalBlockEntity;
import purplecreate.tramways.mixinInterfaces.IRGBItemRenderer;

@Environment(EnvType.CLIENT)
public abstract class StateHolder {
  protected ExtendedSignalState signalState;
  protected JunctionState junctionState;


  public static StateHolder forBlockEntity(SignalBlockEntity be) {
    return new BlockStateHolder(be);
  }

  public static StateHolder forItemStack(ItemStack stack, ItemDisplayContext context, ExtendedSignalState signalState, JunctionState junctionState) {
    return new ItemStateHolder(stack, context, signalState, junctionState);
  }

  public abstract void render(BakedModel model, PoseStack ms, VertexConsumer buffer, int light, int overlay, int r, int g, int b);

  public void render(BakedModel model, PoseStack ms, VertexConsumer buffer, int light, int overlay) {
    render(model, ms, buffer, light, overlay, 255, 255, 255);
  }

  public ExtendedSignalState getSignalState() {
    return signalState;
  }

  public JunctionState getJunctionState() {
    return junctionState;
  }

  private static class BlockStateHolder extends StateHolder {
    private final SignalBlockEntity be;

    public BlockStateHolder(SignalBlockEntity be) {
      this.be = be;
      this.signalState = be.getSignalState();
      this.junctionState = be.getJunctionState();
    }

    @Override
    public void render(BakedModel model, PoseStack ms, VertexConsumer buffer, int light, int overlay, int r, int g, int b) {
      Minecraft.getInstance().getBlockRenderer().getModelRenderer()
        .renderModel(
          ms.last(), buffer, be.getBlockState(), model, r / 255f, g / 255f, b / 255f, light, overlay
        );
    }
  }

  private static class ItemStateHolder extends StateHolder {
    private final ItemStack stack;
    private final ItemDisplayContext context;

    public ItemStateHolder(ItemStack stack, ItemDisplayContext context, ExtendedSignalState signalState, JunctionState junctionState) {
      this.stack = stack;
      this.context = context;
      this.signalState = signalState;
      this.junctionState = junctionState;
    }

    @Override
    public void render(BakedModel model, PoseStack ms, VertexConsumer buffer, int light, int overlay, int r, int g, int b) {
      if (Minecraft.getInstance().getItemRenderer() instanceof IRGBItemRenderer renderer) {
        renderer.tramways$render(stack, context, ms, buffer, light, overlay, model, r, g, b);
      }
    }
  }
}
