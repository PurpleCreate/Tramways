package purplecreate.tramways.content.announcements;

import com.simibubi.create.foundation.block.IBE;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import net.createmod.catnip.math.VoxelShaper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import purplecreate.tramways.TBlockEntities;

public class SpeakerBlock extends DirectionalBlock implements IWrenchable, IBE<SpeakerBlockEntity> {
  private static final VoxelShaper SHAPE =
    new AllShapes.Builder(box(2, 0, 2, 14, 1, 14)).forDirectional();

  public SpeakerBlock(Properties properties) {
    super(properties);
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(FACING);
  }

  @Nullable
  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    BlockState state = super.getStateForPlacement(context);
    if (state == null) return null;
    return state
      .setValue(FACING, context.getClickedFace());
  }

  @Override
  public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
    return SHAPE.get(state.getValue(FACING));
  }

  @Override
  public BlockState rotate(BlockState state, Rotation rotation) {
    return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
  }

  @Override
  public BlockState mirror(BlockState state, Mirror mirror) {
    return state.rotate(mirror.getRotation(state.getValue(FACING)));
  }

  @Override
  public Class<SpeakerBlockEntity> getBlockEntityClass() {
    return SpeakerBlockEntity.class;
  }

  @Override
  public BlockEntityType<? extends SpeakerBlockEntity> getBlockEntityType() {
    return TBlockEntities.SPEAKER.get();
  }
}
