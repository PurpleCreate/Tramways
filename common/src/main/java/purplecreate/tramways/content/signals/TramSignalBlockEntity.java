package purplecreate.tramways.content.signals;

import purplecreate.tramways.TTags;
import purplecreate.tramways.compat.Mods;
import com.railwayteam.railways.content.switches.TrackSwitchBlock;
import com.railwayteam.railways.content.switches.TrackSwitchBlockEntity;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.trains.signal.SignalBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class TramSignalBlockEntity extends SmartBlockEntity {
  BlockEntity current = null;

  public TramSignalBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
  }

  @Override
  public void addBehaviours(List<BlockEntityBehaviour> behaviours) {}

  @Override
  public void lazyTick() {
    super.lazyTick();

    if (level == null) return;

    BlockPos currentPos = worldPosition.below();
    boolean found = false;
    for (int i = 0; i < 16; i++) {
      BlockState state = level.getBlockState(currentPos);
      if (
        AllBlocks.TRACK_SIGNAL.has(state) ||
          Mods.RAILWAYS.isBlock("track_switch_andesite", state) ||
          Mods.RAILWAYS.isBlock("track_switch_brass", state)
      ) {
        current = level.getBlockEntity(currentPos);
        found = true;
        break;
      } else if (!state.is(TTags.SIGNAL_POLE)) {
        break;
      }
      currentPos = currentPos.below();
    }

    if (!found) {
      current = null;
    }
  }

  @Override
  public void tick() {
    super.tick();

    if (level == null) return;

    TramSignalState state = current != null ? getSignalState(current) : TramSignalState.INVALID;

    if (getBlockState().getValue(TramSignalBlock.STATE) != state)
      level.setBlock(
        worldPosition,
        getBlockState().setValue(TramSignalBlock.STATE, state),
        2
      );
  }

  private static TramSignalState getSignalState(BlockEntity blockEntity) {
    TramSignalState state = TramSignalState.INVALID;

    if (blockEntity instanceof SignalBlockEntity signal) {
      if (
        signal.getState() == SignalBlockEntity.SignalState.GREEN
          || signal.getState() == SignalBlockEntity.SignalState.YELLOW
      ) {
        state = TramSignalState.FORWARD;
      } else if (signal.getState() == SignalBlockEntity.SignalState.RED) {
        state = TramSignalState.STOP;
      }
    } else if (blockEntity instanceof TrackSwitchBlockEntity trackSwitch) {
      if (trackSwitch.getState() == TrackSwitchBlock.SwitchState.NORMAL) {
        state = TramSignalState.FORWARD;
      } else if (trackSwitch.getState() == TrackSwitchBlock.SwitchState.REVERSE_LEFT) {
        state = TramSignalState.LEFT;
      } else if (trackSwitch.getState() == TrackSwitchBlock.SwitchState.REVERSE_RIGHT) {
        state = TramSignalState.RIGHT;
      }
    }

    return state;
  }
}
