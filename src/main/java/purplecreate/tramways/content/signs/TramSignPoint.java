package purplecreate.tramways.content.signs;

import com.simibubi.create.Create;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.graph.*;
import com.simibubi.create.content.trains.signal.SignalBlockEntity;
import com.simibubi.create.content.trains.signal.SignalPropagator;
import com.simibubi.create.content.trains.signal.TrackEdgePoint;
import com.simibubi.create.foundation.utility.Couple;
import com.simibubi.create.foundation.utility.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.*;
import java.util.function.BiConsumer;

public class TramSignPoint extends TrackEdgePoint {
  private Couple<Set<BlockPos>> sides = Couple.create(HashSet::new);
  private final Couple<Map<Train, Double>> queue = Couple.create(HashMap::new);

  public void updateTrain(Train train, TrackNode node, double distance) {
    queue.get(isPrimary(node)).put(train, distance);
  }

  public void removeTrain(Train train, TrackNode node) {
    queue.get(isPrimary(node)).remove(train);
  }

  public void iterateQueue(BlockPos pos, BiConsumer<Train, Double> consumer) {
    for (boolean front : Iterate.trueAndFalse) {
      if (!sides.get(front).contains(pos)) continue;
      queue.get(front).forEach(consumer);
    }
  }

  public SignalBlockEntity.OverlayState getOverlayFor(BlockPos pos) {
    for (boolean front : Iterate.trueAndFalse) {
      for (BlockPos iPos : sides.get(front)) {
        if (iPos.equals(pos)) {
          return sides.get(!front).isEmpty()
            ? SignalBlockEntity.OverlayState.RENDER
            : SignalBlockEntity.OverlayState.DUAL;
        }
        return SignalBlockEntity.OverlayState.SKIP;
      }
    }
    return SignalBlockEntity.OverlayState.SKIP;
  }

  @Override
  public boolean canMerge() {
    return true;
  }

  @Override
  public void invalidate(LevelAccessor level) {
    sides.forEach((side) ->
      side.forEach((pos) ->
        invalidateAt(level, pos)));
  }

  @Override
  public boolean canCoexistWith(EdgePointType<?> otherType, boolean front) {
    return otherType == getType();
  }

  @Override
  public void blockEntityAdded(BlockEntity blockEntity, boolean front) {
    sides.get(front).add(blockEntity.getBlockPos());
    notifyTrains(blockEntity.getLevel());
  }

  @Override
  public void blockEntityRemoved(BlockPos blockPos, boolean f) {
    sides.forEach((side) -> side.remove(blockPos));
    if (sides.both(Set::isEmpty))
      removeFromAllGraphs();
  }

  private void notifyTrains(Level level) {
    TrackGraph graph = Create.RAILWAYS.sided(level)
      .getGraph(level, edgeLocation.getFirst());
    if (graph == null)
      return;
    TrackEdge edge = graph.getConnection(edgeLocation.map(graph::locateNode));
    if (edge == null)
      return;
    SignalPropagator.notifyTrains(graph, edge);
  }

  @Override
  public void read(CompoundTag tag, boolean migration, DimensionPalette dimensions) {
    super.read(tag, migration, dimensions);

    if (migration) return;

    sides = Couple.create(HashSet::new);

    for (boolean front : Iterate.trueAndFalse) {
      ListTag posList = tag.getList("Side" + (front ? 1 : 0), Tag.TAG_COMPOUND);
      for (int i = 0; i < posList.size(); i++) {
        CompoundTag current = posList.getCompound(i);
        sides.get(front).add(NbtUtils.readBlockPos(current));
      }
    }
  }

  @Override
  public void write(CompoundTag tag, DimensionPalette dimensions) {
    super.write(tag, dimensions);

    for (boolean front : Iterate.trueAndFalse) {
      ListTag posList = new ListTag();
      for (BlockPos pos : sides.get(front)) {
        posList.add(NbtUtils.writeBlockPos(pos));
      }
      tag.put("Side" + (front ? 1 : 0), posList);
    }
  }
}
