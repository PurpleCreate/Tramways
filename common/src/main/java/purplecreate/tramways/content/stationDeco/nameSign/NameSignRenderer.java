package purplecreate.tramways.content.stationDeco.nameSign;

import com.jozufozu.flywheel.core.PartialModel;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import com.simibubi.create.foundation.model.BakedModelHelper;
import com.simibubi.create.foundation.render.BakedModelRenderHelper;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.RegisteredObjects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import purplecreate.tramways.TPartialModels;
import purplecreate.tramways.content.stationDeco.nameSign.info.NameSignInfo;

import java.util.List;

@Environment(EnvType.CLIENT)
public class NameSignRenderer extends SmartBlockEntityRenderer<NameSignBlockEntity> {
  private static final PartialModel woodenInnerA = TPartialModels.SIGN_WOODEN_INNER_A;
  private static final PartialModel woodenInnerB = TPartialModels.SIGN_WOODEN_INNER_B;

  public NameSignRenderer(BlockEntityRendererProvider.Context context) {
    super(context);
  }

  // https://github.com/Creators-of-Create/Create/blob/d48a504486311f3175f4ebef3b0649140e728fbb/src/main/java/com/simibubi/create/content/kinetics/waterwheel/WaterWheelRenderer.java#L147
  private TextureAtlasSprite getSpriteOnSide(BlockState state, Direction side) {
    BakedModel model = Minecraft.getInstance()
      .getBlockRenderer()
      .getBlockModel(state);
    if (model == null)
      return null;
    RandomSource random = RandomSource.create();
    random.setSeed(42L);
    List<BakedQuad> quads = model.getQuads(state, side, random);
    if (!quads.isEmpty()) {
      return quads.get(0)
        .getSprite();
    }
    random.setSeed(42L);
    quads = model.getQuads(state, null, random);
    if (!quads.isEmpty()) {
      for (BakedQuad quad : quads) {
        if (quad.getDirection() == side) {
          return quad.getSprite();
        }
      }
    }
    return model.getParticleIcon();
  }

  public static void renderText(
    NameSignInfo.Entry nameSignInfo,
    List<String> lines,
    PoseStack ms,
    MultiBufferSource buffer,
    int light,
    boolean renderOnBlock
  ) {
    Font fontRenderer = Minecraft.getInstance().font;

    if (renderOnBlock) {
      float scale = 0.01f;
      float tx = switch (nameSignInfo.align()) {
        case LEFT -> 0f;
        case RIGHT -> 1f;
        case CENTER -> .5f;
      };

      ms.translate(tx, .5, 0);
      ms.scale(scale, -scale, scale);
      ms.translate(tx, .5, 0);
    }

    for (int i = 0; i < lines.size(); i++) {
      String line = lines.get(i);
      float x = switch (nameSignInfo.align()) {
        case LEFT -> nameSignInfo.offset();
        case RIGHT -> -nameSignInfo.offset();
        case CENTER -> (fontRenderer.width(line) / -2f) + nameSignInfo.offset();
      };
      float y = i * fontRenderer.lineHeight - (lines.size() * fontRenderer.lineHeight / 2f);

      fontRenderer.drawInBatch(
        line,
        x,
        y,
        nameSignInfo.color().getTextColor(),
        false,
        ms.last().pose(),
        buffer,
        Font.DisplayMode.NORMAL,
        0,
        light
      );
    }
  }

  private void renderWoodenInner(
    BlockState wood,
    BlockState ref,
    Direction direction,
    int light,
    PoseStack ms,
    MultiBufferSource buffer
  ) {
    // a (fence, not rotated)
    BakedModel modelA = BakedModelHelper.generateModel(
      woodenInnerA.get(),
      s -> getSpriteOnSide(wood, Direction.UP)
    );
    BakedModelRenderHelper.standardModelRender(modelA, ref)
      .light(light)
      .renderInto(ms, buffer.getBuffer(RenderType.solid()));

    // b (block, rotated)
    ms.pushPose();

    TransformStack.cast(ms)
      .centre()
      .rotateY(AngleHelper.horizontalAngle(direction))
      .unCentre();

    BakedModel modelB = BakedModelHelper.generateModel(
      woodenInnerB.get(),
      s -> getSpriteOnSide(wood, Direction.UP)
    );
    BakedModelRenderHelper.standardModelRender(modelB, ref)
      .light(light)
      .renderInto(ms, buffer.getBuffer(RenderType.solid()));

    ms.popPose();
  }

  @Override
  protected void renderSafe(
    NameSignBlockEntity be,
    float partialTicks,
    PoseStack ms,
    MultiBufferSource buffer,
    int light,
    int overlay
  ) {
    BlockState state = be.getBlockState();

    Direction facing = state.getValue(NameSignBlock.FACING);
    boolean extended = state.getValue(NameSignBlock.EXTENDED);

    NameSignInfo.Entry nameSignInfo = NameSignInfo.get(
      RegisteredObjects.getKeyOrThrow(state.getBlock())
    );

    renderWoodenInner(be.wood, be.getBlockState(), facing, light, ms, buffer);

    if (extended && facing.getAxisDirection() == Direction.AxisDirection.NEGATIVE)
      return;

    for (Direction direction : List.of(facing, facing.getOpposite())) {
      ms.pushPose();
      float extendedTranslation = direction == facing ? 1 / 2f : -1 / 2f;
      TransformStack.cast(ms)
        .centre()
        .rotateY(AngleHelper.horizontalAngle(direction))
        .unCentre()
        .translate(extended ? extendedTranslation : 0, 0, 11.01 / 16f);
      renderText(
        nameSignInfo.forceCenteredIf(extended),
        be.getLinesSafe(),
        ms,
        buffer,
        light,
        true
      );
      ms.popPose();
    }
  }
}
