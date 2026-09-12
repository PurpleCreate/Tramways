package purplecreate.tramways.content.signals.block;

import com.simibubi.create.AllShapes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IHaveBigOutline;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import purplecreate.tramways.Tramways;

import java.util.HashMap;
import java.util.Map;

public abstract class SignAttachedToPoleBlock extends HorizontalDirectionalBlock implements IWrenchable, IHaveBigOutline {
  public static final BooleanProperty GIRDER = BooleanProperty.create("girder");
  public static final IntegerProperty OFFSET = IntegerProperty.create("offset", 1, 16);

  private static final Map<Block, Integer> attachmentPoleCache = new HashMap<>();

  protected SignAttachedToPoleBlock(Properties properties) {
    super(properties);

    registerDefaultState(
      defaultBlockState()
        .setValue(FACING, Direction.NORTH)
        .setValue(GIRDER, true)
        .setValue(OFFSET, 16)
    );
  }

  public static TagKey<Block> tagForPoleSized(int px) {
    if (px < 1 || px > 16) throw new IndexOutOfBoundsException(px);
    return TagKey.create(BuiltInRegistries.BLOCK.key(), Tramways.rl("attachment_pole/" + px + "px"));
  }

  public static int isAttachmentPole(BlockGetter level, BlockPos polePos, Direction poleFace) {
    BlockState poleBlock = level.getBlockState(polePos);

    if (attachmentPoleCache.containsKey(poleBlock.getBlock()))
      return attachmentPoleCache.get(poleBlock.getBlock());

    if (poleBlock.isFaceSturdy(level, polePos, poleFace)) {
      attachmentPoleCache.put(poleBlock.getBlock(), 16);
      return 16;
    }

    for (int i = 1; i < 17; i++) {
      if (poleBlock.is(tagForPoleSized(i))) {
        attachmentPoleCache.put(poleBlock.getBlock(), i);
        return i;
      }
    }

    attachmentPoleCache.put(poleBlock.getBlock(), -1);
    return -1;
  }

  public Vec3 getRenderOffset(BlockState state) {
    Direction facing = state.getValue(FACING).getOpposite();
    double offset = state.getValue(GIRDER) ? -12 : (16 - state.getValue(OFFSET)) / 2.0;
    return Vec3.atLowerCornerOf(facing.getNormal()).scale(offset / 16);
  }

  public BlockPos getPole(BlockState state, BlockPos pos) {
    if (state.getValue(GIRDER)) return pos;
    return pos.relative(state.getValue(FACING).getOpposite());
  }

  protected abstract VoxelShape getFaceShape(Direction direction);

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(FACING).add(GIRDER).add(OFFSET);
  }

  @Override
  public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
    BlockState state = super.getStateForPlacement(context);
    if (state == null) return null;

    Level level = context.getLevel();
    Direction face = context.getClickedFace();
    BlockPos pos = context.getClickedPos().relative(face.getOpposite());

    int offset = context.getClickedFace().getAxis() == Direction.Axis.Y
      ? -1
      : isAttachmentPole(level, pos, face);

    return state
      .setValue(FACING, context.getHorizontalDirection().getOpposite())
      .setValue(GIRDER, offset == -1)
      .setValue(OFFSET, offset == -1 ? 16 : offset);
  }

  @Override
  public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
    if (state.getValue(GIRDER)) return true;
    return isAttachmentPole(level, getPole(state, pos), state.getValue(FACING)) == state.getValue(OFFSET);
  }

  @Override
  public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
    if (state.getValue(GIRDER)) return AllShapes.EIGHT_VOXEL_POLE.get(Direction.Axis.Y);

    Direction facing = state.getValue(FACING);
    Vec3 offset = getRenderOffset(state);
    return getFaceShape(facing).move(offset.x, offset.y, offset.z);
  }

  @Override
  public VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
    return getShape(state, level, pos, CollisionContext.empty());
  }
}
