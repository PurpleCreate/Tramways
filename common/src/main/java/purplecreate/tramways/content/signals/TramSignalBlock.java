package purplecreate.tramways.content.signals;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import purplecreate.tramways.TBlockEntities;
import com.simibubi.create.AllShapes;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class TramSignalBlock extends HorizontalDirectionalBlock implements IBE<TramSignalBlockEntity>, IWrenchable {
  public static final EnumProperty<TramSignalState> STATE = EnumProperty.create("state", TramSignalState.class);

  public TramSignalBlock(Properties properties) {
    super(properties);
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(FACING).add(STATE);
  }

  @Nullable
  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    BlockState state = super.getStateForPlacement(context);
    if (state == null) return null;

    return state
      .setValue(FACING, context.getHorizontalDirection().getOpposite())
      .setValue(STATE, TramSignalState.INVALID);
  }

  @Override
  public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
    return AllShapes.EIGHT_VOXEL_POLE.get(Direction.Axis.Y);
  }

  @Override
  public Class<TramSignalBlockEntity> getBlockEntityClass() {
    return TramSignalBlockEntity.class;
  }

  @Override
  public BlockEntityType<? extends TramSignalBlockEntity> getBlockEntityType() {
    return TBlockEntities.TRAM_SIGNAL.get();
  }
}
