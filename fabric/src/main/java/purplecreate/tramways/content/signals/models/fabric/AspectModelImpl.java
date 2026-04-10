package purplecreate.tramways.content.signals.models.fabric;

import com.simibubi.create.foundation.model.BakedModelHelper;
import net.createmod.catnip.data.Iterate;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.impl.client.indigo.renderer.mesh.MutableQuadViewImpl;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import purplecreate.tramways.TPartialModels;

import java.util.ArrayList;
import java.util.List;

public class AspectModelImpl extends ForwardingBakedModel {
  private final float radius;

  private AspectModelImpl(float radius) {
    this.wrapped = TPartialModels.ASPECT.get();
    this.radius = radius;
  }

  public static BakedModel create(float radius) {
    return new AspectModelImpl(radius);
  }

  @Override
  public boolean isVanillaAdapter() {
    return true;
  }

  @Override
  public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand) {
    List<BakedQuad> quads = new ArrayList<>();

    float r = radius / 16;
    float ir = 1 - r;
    AABB bb = new AABB(0, 0, 0, r, r, 0);

    for (BakedQuad quad : wrapped.getQuads(state, side, rand)) {
      if (quad.getDirection() != Direction.NORTH) continue;

      for (boolean top : Iterate.trueAndFalse) {
        for (boolean right : Iterate.trueAndFalse) {
          AABB bb1 = bb;
          Vec3 offset = new Vec3((right ? r : ir) - 1, (top ? r : ir) - 1, 0);

          if (top)
            bb1 = bb1.move(0, ir, 0);
          if (right)
            bb1 = bb1.move(ir, 0, 0);

          MutableQuadView quadView = RendererAccess.INSTANCE.getRenderer().meshBuilder().getEmitter();

          quadView
            .fromVanilla(quad.getVertices(), 0)
            .nominalFace(quad.getDirection())
            .colorIndex(quad.getTintIndex())
            .material(null)
            .tag(0);

          BakedModelHelper.cropAndMove(quadView, quad.getSprite(), bb1, offset);
          quads.add(quadView.toBakedQuad(quad.getSprite()));
        }
      }
    }

    return quads;
  }
}
