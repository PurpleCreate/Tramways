package purplecreate.tramways.content.signals.block;

import com.simibubi.create.foundation.block.IBE;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import purplecreate.tramways.content.signals.base.SignalType;

public class SignalBlock extends SignAttachedToPoleBlock implements IBE<SignalBlockEntity> {
  private final SignalType signalType;
  private RegistryEntry<BlockEntityType<?>> blockEntity;

  private SignalBlock(Properties properties, SignalType signalType) {
    super(properties);
    this.signalType = signalType;
  }

  public static NonNullFunction<Properties, SignalBlock> of(SignalType signalType) {
    return properties -> new SignalBlock(properties, signalType);
  }

  @Override
  public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
    return onBlockEntityUse(level, pos, be -> {
      player.sendSystemMessage(Component.literal(
        (level.isClientSide ? "cleint" : "server") + " "
          + be.getSignalState() + " "
          + be.getJunctionState()));

      return InteractionResult.SUCCESS;
    });
  }

  @Override
  public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
    IBE.onRemove(state, level, pos, newState);
  }

  @Override
  protected VoxelShape getFaceShape(Direction direction) {
    return signalType.getFaceShape(direction);
  }

  @Override
  public Class<SignalBlockEntity> getBlockEntityClass() {
    return SignalBlockEntity.class;
  }

  @Override
  public BlockEntityType<? extends SignalBlockEntity> getBlockEntityType() {
    return (BlockEntityType<? extends SignalBlockEntity>)blockEntity.get();
  }

  public void setBlockEntityEntry(RegistryEntry<BlockEntityType<?>> blockEntity) {
    this.blockEntity = blockEntity;
  }
}
