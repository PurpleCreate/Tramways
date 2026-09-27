package purplecreate.tramways.content.requestStop.station;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.phys.BlockHitResult;
import purplecreate.tramways.TBlockEntities;
import purplecreate.tramways.content.requestStop.RequestStopServer;

public class RequestStopButtonBlock extends ButtonBlock implements IBE<RequestStopButtonBlockEntity>, IWrenchable {
  public RequestStopButtonBlock(Properties properties) {
    super(BlockSetType.STONE, 0, properties);
  }

  @Override
  protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
    if (state.getValue(POWERED))
      return InteractionResult.CONSUME;

    playSound(player, level, pos, true);
    level.setBlock(pos, state.setValue(POWERED, true), 3);

    if (!level.isClientSide)
      withBlockEntityDo(level, pos, be -> {
        if (be.getLinkedStation() != null) {
          Train train = be.getLinkedStation().getNearestTrain();
          if (train != null) {
            RequestStopServer.request(train);
            AllSoundEvents.CONFIRM.playOnServer(level, pos);
          }
        }
      });

    return InteractionResult.SUCCESS;
  }

  @Override
  public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
    return 0;
  }

  @Override
  public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
    return 0;
  }

  @Override
  public Class<RequestStopButtonBlockEntity> getBlockEntityClass() {
    return RequestStopButtonBlockEntity.class;
  }

  @Override
  public BlockEntityType<? extends RequestStopButtonBlockEntity> getBlockEntityType() {
    return TBlockEntities.REQUEST_STOP_BUTTON.get();
  }
}
