package purplecreate.tramways.content.signs;

import net.createmod.catnip.math.VoxelShaper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import purplecreate.tramways.TBlockEntities;
import com.simibubi.create.AllShapes;
import com.simibubi.create.foundation.block.IBE;
import net.createmod.catnip.gui.ScreenOpener;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TramSignBlock extends SignAttachedToPoleBlock implements IBE<TramSignBlockEntity> {
  private static final VoxelShaper shape = new AllShapes.Builder(box(2, 2, 14, 14, 14, 16)).forHorizontal(Direction.NORTH);

  public enum SignType {
    TRAM,
    RAILWAY,
    AUXILIARY,
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

  public static TramSignBlock newAuxiliarySign(Properties properties) {
    return new TramSignBlock(SignType.AUXILIARY, properties);
  }

  @Override
  protected VoxelShape getFaceShape(BlockState state, BlockGetter level, BlockPos pos) {
    return shape.get(state.getValue(FACING));
  }

  @Override
  @Environment(EnvType.CLIENT)
  public InteractionResult onWrenched(BlockState state, UseOnContext context) {
    TramSignBlockEntity be = getBlockEntity(context.getLevel(), context.getClickedPos());
    if (be != null)
      ScreenOpener.open(new TramSignSettingsScreen(be));
    return InteractionResult.SUCCESS;
  }

  @Override
  public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
    IBE.onRemove(state, level, pos, newState);
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
