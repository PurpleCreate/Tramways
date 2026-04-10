package purplecreate.tramways.content.signals.models.fabric;

import com.simibubi.create.content.decoration.girder.ConnectedGirderModel;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;
import purplecreate.tramways.TPartialModels;
import purplecreate.tramways.content.signals.block.SignAttachedToPoleBlock;

import java.util.function.Supplier;

public class SignAttachedToPoleModelImpl extends ForwardingBakedModel {
  private ConnectedGirderModel girder;

  private SignAttachedToPoleModelImpl(BakedModel wrapped) {
    this.wrapped = wrapped;
  }

  public static BakedModel create(BakedModel wrapped) {
    return new SignAttachedToPoleModelImpl(wrapped);
  }

  private void initGirder() {
    girder = new ConnectedGirderModel(TPartialModels.GIRDER.get());
  }

  @Override
  public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
    if (state == null || !(state.getBlock() instanceof SignAttachedToPoleBlock block)) {
      super.emitBlockQuads(blockView, state, pos, randomSupplier, context);
      return;
    }

    initGirder();
    Vector3f offset = block.getRenderOffset(state).toVector3f();

    context.pushTransform(quad -> {
      for (int i = 0; i < 4; i++) {
        quad.pos(i, new Vector3f(quad.x(i), quad.y(i), quad.z(i)).add(offset));
      }
      return true;
    });
    super.emitBlockQuads(blockView, state, pos, randomSupplier, context);
    context.popTransform();

    if (state.getValue(SignAttachedToPoleBlock.GIRDER)) {
      girder.emitBlockQuads(blockView, state, pos, randomSupplier, context);
    }
  }
}
