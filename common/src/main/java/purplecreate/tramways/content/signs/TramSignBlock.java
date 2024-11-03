package purplecreate.tramways.content.signs;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import purplecreate.tramways.TBlockEntities;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.gui.ScreenOpener;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import purplecreate.tramways.util.Env;

public class TramSignBlock extends HorizontalDirectionalBlock implements IBE<TramSignBlockEntity>, IWrenchable {
  public enum SignType {
    TRAM,
    RAILWAY,
  }

  public final SignType signType;

  private TramSignBlock(SignType signType, Properties properties) {
    super(properties);
    this.signType = signType;
  }

  public static TramSignBlock newTramSign(Properties properties) {
    return new TramSignBlock(SignType.TRAM, properties);
  }

  public static TramSignBlock newRailwaySign(Properties properties) {
    return new TramSignBlock(SignType.RAILWAY, properties);
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
      .setValue(FACING, context.getHorizontalDirection().getOpposite());
  }

  @Override
  public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
    return AllShapes.EIGHT_VOXEL_POLE.get(Direction.Axis.Y);
  }

  @Override
  @Environment(EnvType.CLIENT)
  public InteractionResult onWrenched(BlockState state, UseOnContext context) {
    Env.unsafeRunWhenOn(Env.CLIENT, () -> () ->
      withBlockEntityDo(context.getLevel(), context.getClickedPos(), (be) ->
        ScreenOpener.open(new TramSignSettingsScreen(be))
      )
    );

    return InteractionResult.SUCCESS;
  }

  @Override
  public Class<TramSignBlockEntity> getBlockEntityClass() {
    return TramSignBlockEntity.class;
  }

  @Override
  public BlockEntityType<? extends TramSignBlockEntity> getBlockEntityType() {
    return TBlockEntities.TRAM_SIGN.get();
  }
}
