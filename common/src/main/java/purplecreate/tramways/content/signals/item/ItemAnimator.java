package purplecreate.tramways.content.signals.item;

import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import purplecreate.tramways.content.signals.base.ExtendedSignalState;
import purplecreate.tramways.content.signals.base.JunctionState;
import purplecreate.tramways.content.signals.base.StateHolder;

import java.util.ArrayList;
import java.util.List;

public class ItemAnimator {
  private final List<Instruction> instructions = new ArrayList<>();
  private float totalDuration = 0;

  private void add(Instruction instruction) {
    instructions.add(instruction);
    totalDuration += instruction.waitSeconds;
  }

  public void sleep(float seconds) {
    add(new Instruction(seconds, null, null, Instruction.UPDATE_NOTHING));
  }

  public void setSignal(ExtendedSignalState state) {
    add(new Instruction(0, state, null, Instruction.UPDATE_SIGNAL_STATE));
  }

  public void setRoute(JunctionState state) {
    add(new Instruction(0, null, state, Instruction.UPDATE_JUNCTION_STATE));
  }

  public StateHolder getStateForTime(ItemStack stack, ItemDisplayContext context, float renderTime) {
    float countTime = 0;
    ExtendedSignalState signalState = ExtendedSignalState.INVALID;
    JunctionState junctionState = null;

    for (Instruction instruction : instructions) {
      countTime += instruction.waitSeconds * 20;

      if (instruction.isFlagSet(Instruction.UPDATE_SIGNAL_STATE)) {
        signalState = instruction.signalState;
      }

      if (instruction.isFlagSet(Instruction.UPDATE_JUNCTION_STATE)) {
        junctionState = instruction.junctionState;
      }

      if (countTime >= renderTime % (totalDuration * 20)) {
        break;
      }
    }

    return StateHolder.forItemStack(stack, context, signalState, junctionState);
  }

  private record Instruction(float waitSeconds, ExtendedSignalState signalState, JunctionState junctionState, byte flags) {
    public static final byte UPDATE_NOTHING = 0;
    public static final byte UPDATE_SIGNAL_STATE = 1;
    public static final byte UPDATE_JUNCTION_STATE = 2;

    public boolean isFlagSet(byte flag) {
      return (flags & flag) == flag;
    }
  }
}
