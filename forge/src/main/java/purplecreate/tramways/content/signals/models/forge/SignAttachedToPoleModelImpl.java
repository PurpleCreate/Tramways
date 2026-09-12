package purplecreate.tramways.content.signals.models.forge;

import com.simibubi.create.content.decoration.girder.ConnectedGirderModel;
import com.simibubi.create.foundation.model.BakedQuadHelper;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import purplecreate.tramways.TPartialModels;
import purplecreate.tramways.content.signs.SignAttachedToPoleBlock;

import java.util.ArrayList;
import java.util.List;

public class SignAttachedToPoleModelImpl extends BakedModelWrapper<BakedModel> {
  private ConnectedGirderModel girder;

  private SignAttachedToPoleModelImpl(BakedModel originalModel) {
    super(originalModel);
  }

  public static BakedModel create(BakedModel wrapped) {
    return new SignAttachedToPoleModelImpl(wrapped);
  }

  private void initGirder() {
    girder = new ConnectedGirderModel(TPartialModels.GIRDER.get());
  }

  @Override
  public @NotNull ModelData getModelData(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ModelData modelData) {
    initGirder();
    ModelData girderData = girder.getModelData(level, pos, state, modelData);
    ModelData.Builder data = super.getModelData(level, pos, state, modelData).derive();

    for (ModelProperty property : girderData.getProperties()) {
      data.with(property, girderData.get(property));
    }

    return data.build();
  }

  @Override
  public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData extraData, @Nullable RenderType renderType) {
    if (state == null || !(state.getBlock() instanceof SignAttachedToPoleBlock block))
      return originalModel.getQuads(null, side, rand);

    initGirder();
    List<BakedQuad> quads = new ArrayList<>();
    Vec3 offset = block.getRenderOffset(state);

    for (BakedQuad templateQuad : originalModel.getQuads(null, side, rand)) {
      BakedQuad quad = BakedQuadHelper.clone(templateQuad);
      int[] data = quad.getVertices();
      for (int i = 0; i < 4; i++) {
        BakedQuadHelper.setXYZ(data, i, BakedQuadHelper.getXYZ(data, i).add(offset));
      }
      quads.add(quad);
    }

    if (state.getValue(SignAttachedToPoleBlock.GIRDER)) {
      quads.addAll(girder.getQuads(state, side, rand, extraData, renderType));
    }

    return quads;
  }
}
