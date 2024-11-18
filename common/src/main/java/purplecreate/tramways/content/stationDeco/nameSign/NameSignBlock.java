package purplecreate.tramways.content.stationDeco.nameSign;

import com.simibubi.create.AllShapes;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.gui.ScreenOpener;
import com.simibubi.create.foundation.utility.VoxelShaper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import purplecreate.tramways.TBlockEntities;
import purplecreate.tramways.util.Env;

public class NameSignBlock extends HorizontalDirectionalBlock implements IBE<NameSignBlockEntity> {
  public static final VoxelShaper shape =
    new AllShapes.Builder(Block.box(6, 0, 6, 10, 16, 10))
      .add(Block.box(0, 4, 5, 16, 12, 11))
      .forHorizontal(Direction.NORTH);

  public NameSignBlock(Properties properties) {
    super(properties);
  }

  @Override
  public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
    if (player.getItemInHand(hand).isEmpty()) {
      openScreen(level, pos);
      return InteractionResult.SUCCESS;
    }

    return onBlockEntityUse(level, pos, (be) -> be.applyItem(player.getItemInHand(hand)));
  }

  @Environment(EnvType.CLIENT)
  public void openScreen(Level level, BlockPos pos) {
    Env.unsafeRunWhenOn(Env.CLIENT, () -> () ->
      withBlockEntityDo(level, pos, (be) ->
        ScreenOpener.open(new NameSignScreen(be))
      )
    );
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(FACING);
  }

  @Override
  public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
    BlockState state = super.getStateForPlacement(context);
    if (state == null) return null;
    return state.setValue(FACING, context.getHorizontalDirection());
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
