package purplecreate.tramways.content.signals.block;

import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import purplecreate.tramways.TBlockEntities;
import purplecreate.tramways.content.signals.render.SignalType;
import purplecreate.tramways.content.signs.SignAttachedToPoleBlock;
import purplecreate.tramways.util.Env;

import java.util.Optional;

public class GenericSignalBlock extends SignAttachedToPoleBlock implements IBE<GenericSignalBlockEntity> {
  public GenericSignalBlock(Properties properties) {
    super(properties);
  }

  @Override
  public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
    IBE.onRemove(state, level, pos, newState);
  }

  // return empty on server or best possible result on client
  @Override
  protected VoxelShape getFaceShape(BlockState state, BlockGetter blockGetter, BlockPos pos) {
    Direction facing = state.getValue(FACING);

    if (blockGetter instanceof Level level && level.isClientSide) {
      Optional<GenericSignalBlockEntity> be = getBlockEntityOptional(level, pos);

      if (be.isPresent()) {
        return Env.unsafeEvaluateWhenOn(Env.CLIENT, () -> () -> {
          SignalType signalType = be.get().getSignalType();
          return signalType != null
            ? signalType.getShape(facing)
            : Shapes.block();
        }, Shapes.empty());
      }

      return Shapes.block();
    }

    return Shapes.empty();
  }

  @Override
  public Class<GenericSignalBlockEntity> getBlockEntityClass() {
    return GenericSignalBlockEntity.class;
  }

  @Override
  public BlockEntityType<? extends GenericSignalBlockEntity> getBlockEntityType() {
    return TBlockEntities.GENERIC_SIGNAL.get();
  }
}
