package purplecreate.tramways.content.stationDeco.nameSign;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.AllShapes;
import com.simibubi.create.foundation.block.IBE;
import net.createmod.catnip.gui.ScreenOpener;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.VoxelShaper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import purplecreate.tramways.TBlockEntities;
import purplecreate.tramways.util.Env;

import java.util.List;

public class NameSignBlock extends HorizontalDirectionalBlock implements IBE<NameSignBlockEntity> {
  private static final MapCodec<NameSignBlock> CODEC = simpleCodec(NameSignBlock::new);
  public static final BooleanProperty EXTENDED = BlockStateProperties.EXTENDED;

  public static final VoxelShaper shape =
    new AllShapes.Builder(Block.box(6, 0, 6, 10, 16, 10))
      .add(Block.box(0, 4, 5, 16, 12, 11))
      .forHorizontal(Direction.NORTH);

  public NameSignBlock(Properties properties) {
    super(properties);

  }

  @Override
  protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
    return CODEC;
  }

  @Override
  protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
    Env.unsafeRunWhenOn(Env.CLIENT, () -> () ->
      openScreen(level, pos)
    );
    return InteractionResult.SUCCESS;
  }

  @Override
  protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
    return onBlockEntityUseItemOn(level, pos, (be) -> be.applyItem(stack));
  }

  @Environment(EnvType.CLIENT)
  public void openScreen(Level level, BlockPos pos) {
    BlockState state = level.getBlockState(pos);
    Direction facing = state.getValue(FACING);
    BlockPos actualPos = pos;

    if (
      state.getValue(EXTENDED)
        && state.getValue(FACING).getAxisDirection() == Direction.AxisDirection.NEGATIVE
    )
      actualPos = pos.relative(facing.getCounterClockWise());

    NameSignBlockEntity be = getBlockEntity(level, actualPos);
    if (be != null)
      ScreenOpener.open(new NameSignScreen(be));
  }

  @Override
  public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
    super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);

    if (state.getValue(EXTENDED)) {
      BlockPos connectedPos = pos.relative(state.getValue(FACING).getCounterClockWise());
      BlockState connectedState = level.getBlockState(connectedPos);

      if (connectedState.getBlock() != state.getBlock()) {
        level.setBlock(pos, state.setValue(EXTENDED, false), 2);
        withBlockEntityDo(level, pos, (be) -> be.lines = List.of("", "", "", ""));
      }
    } else {
      BlockState neighborState = level.getBlockState(neighborPos);
      Direction neighborFacing = neighborState.getValue(FACING);

      if (
        neighborState.getBlock() == state.getBlock()
          && neighborState.getValue(EXTENDED)
          && neighborPos.relative(neighborFacing.getCounterClockWise()).equals(pos)
      )
        level.setBlock(
          pos,
          state
            .setValue(FACING, neighborFacing.getOpposite())
            .setValue(EXTENDED, true),
          2
        );
    }
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(FACING).add(EXTENDED);
  }

  @Override
  public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
    BlockState state = super.getStateForPlacement(context);
    if (state == null) return null;

    BlockPos pos = context.getClickedPos();
    Level level = context.getLevel();
    boolean extended = false;
    Direction facing = context.getHorizontalDirection();

    for (int i : Iterate.positiveAndNegative) {
      Direction.Axis axis = facing.getClockWise().getAxis();
      Direction.AxisDirection axisDir = i == 1
        ? Direction.AxisDirection.POSITIVE
        : Direction.AxisDirection.NEGATIVE;

      BlockPos checkPos = pos.relative(axis, i);
      BlockState checkState = level.getBlockState(checkPos);

      if (checkState.getBlock() == state.getBlock() && !checkState.getValue(EXTENDED)) {
        extended = true;
        facing = Direction.get(
          axis == Direction.Axis.X
            ? axisDir
            : axisDir.opposite(),
          facing.getAxis()
        );
        break;
      }
    }

    return state
      .setValue(EXTENDED, extended)
      .setValue(FACING, facing);
  }

  @Override
  public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
    return shape.get(state.getValue(FACING));
  }

  @Override
  public Class<NameSignBlockEntity> getBlockEntityClass() {
    return NameSignBlockEntity.class;
  }

  @Override
  public BlockEntityType<? extends NameSignBlockEntity> getBlockEntityType() {
    return TBlockEntities.STATION_NAME_SIGN.get();
  }
}
