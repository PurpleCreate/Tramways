package purplecreate.tramways.content.signals.models.forge;

import com.simibubi.create.foundation.model.BakedModelHelper;
import com.simibubi.create.foundation.model.BakedQuadHelper;
import net.createmod.catnip.data.Iterate;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import purplecreate.tramways.TPartialModels;

import java.util.ArrayList;
import java.util.List;

public class AspectModelImpl extends BakedModelWrapper<BakedModel> {
  private final float radius;

  public AspectModelImpl(float radius) {
    super(TPartialModels.ASPECT.get());
    this.radius = radius;
  }

  public static BakedModel create(float radius) {
    return new AspectModelImpl(radius);
  }

  @Override
  public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand) {
    List<BakedQuad> quads = new ArrayList<>();

    float r = radius / 16;
    float ir = 1 - r;
    AABB bb = new AABB(0, 0, 0, r, r, 0);

    for (BakedQuad quad : originalModel.getQuads(state, side, rand)) {
      if (quad.getDirection() != Direction.NORTH) continue;

      for (boolean top : Iterate.trueAndFalse) {
        for (boolean right : Iterate.trueAndFalse) {
          AABB bb1 = bb;
          Vec3 offset = new Vec3((right ? r : ir) - 1, (top ? r : ir) - 1, 0);

          if (top)
            bb1 = bb1.move(0, ir, 0);
          if (right)
            bb1 = bb1.move(ir, 0, 0);

          quads.add(BakedQuadHelper.cloneWithCustomGeometry(
            quad,
            BakedModelHelper.cropAndMove(quad.getVertices(), quad.getSprite(), bb1, offset)
          ));
        }
      }
    }

    return quads;
  }

  @Override
  public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand, ModelData extraData, RenderType renderType) {
    return getQuads(state, side, rand);
  }
}
