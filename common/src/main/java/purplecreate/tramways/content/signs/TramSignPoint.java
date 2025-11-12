package purplecreate.tramways.content.signs;

import com.simibubi.create.Create;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.graph.*;
import com.simibubi.create.content.trains.signal.SignalBlockEntity;
import com.simibubi.create.content.trains.signal.SignalPropagator;
import com.simibubi.create.content.trains.signal.TrackEdgePoint;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import purplecreate.tramways.content.signs.demands.SignDemand;

import java.util.*;

public class TramSignPoint extends TrackEdgePoint {
  private Couple<Set<SignData>> sides = Couple.create(HashSet::new);

  public SignData getSignData(BlockPos pos) {
    for (boolean front : Iterate.trueAndFalse) {
      for (SignData sign : sides.get(front)) {
        if (!sign.pos.equals(pos)) continue;
        return sign;
      }
    }

    return null;
  }

  public Set<SignData> getSignData(boolean primary) {
    return sides.get(primary);
  }

  public void updateSignData(BlockPos pos, SignDemand demand, CompoundTag demandExtra) {
    for (boolean front : Iterate.trueAndFalse) {
      for (SignData sign : sides.get(front)) {
        if (!sign.pos.equals(pos)) continue;
        sign.demand = demand;
        sign.demandExtra = demandExtra;
        return;
      }
    }
  }

  public SignalBlockEntity.OverlayState getOverlayFor(BlockPos pos) {
    for (boolean front : Iterate.trueAndFalse) {
      for (SignData sign : sides.get(front)) {
        if (sign.pos.equals(pos)) {
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
      side.forEach((sign) ->
        invalidateAt(level, sign.pos)));
  }

  @Override
  public boolean canCoexistWith(EdgePointType<?> otherType, boolean front) {
    return otherType == getType();
  }

  @Override
  public void blockEntityAdded(BlockEntity blockEntity, boolean front) {
    SignData sign = SignData.of(blockEntity);
    if (sign != null) {
      sides.get(front).add(sign);
      notifyTrains(blockEntity.getLevel());
    }
  }

  @Override
  public void blockEntityRemoved(BlockPos blockPos, boolean f) {
    sides.forEach((side) ->
      side.removeIf((sign) ->
        sign.pos.equals(blockPos)
      )
    );

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
      ListTag dataList = tag.getList("DemandData" + (front ? 1 : 0), Tag.TAG_COMPOUND);

      for (int i = 0; i < posList.size(); i++) {
        CompoundTag posTag = posList.getCompound(i);
        CompoundTag dataTag = dataList.getCompound(i);

        SignData sign = new SignData();
        sign.read(posTag, dataTag);

        sides.get(front).add(sign);
      }
    }
  }

  @Override
  public void write(CompoundTag tag, DimensionPalette dimensions) {
    super.write(tag, dimensions);

    for (boolean front : Iterate.trueAndFalse) {
      ListTag posList = new ListTag();
      ListTag dataList = new ListTag();

      for (SignData sign : sides.get(front)) {
        CompoundTag posTag = new CompoundTag();
        CompoundTag dataTag = new CompoundTag();

        sign.write(posTag, dataTag);

        posList.add(posTag);
        dataList.add(dataTag);
      }

      tag.put("Side" + (front ? 1 : 0), posList);
      tag.put("DemandData" + (front ? 1 : 0), dataList);
    }
  }

  public static class SignData {
    BlockPos pos;
    SignDemand demand;
    CompoundTag demandExtra;

    SignData() {
      this(null, null, null);
    }

    SignData(BlockPos pos, SignDemand demand, CompoundTag demandExtra) {
      this.pos = pos;
      this.demand = demand;
      this.demandExtra = demandExtra;
    }

    public static SignData of(BlockEntity be) {
      return be instanceof TramSignBlockEntity sbe
        ? new SignData(sbe.getBlockPos(), sbe.getDemand(), sbe.getDemandExtra())
        : null;
    }

    public SignDemand.Result execute(Train train, double distance) {
      return demand.execute(demandExtra, train, distance);
    }

    public void read(CompoundTag posTag, CompoundTag dataTag) {
      pos = NbtUtils.readBlockPos(posTag);

      if (dataTag.contains("Demand"))
        demand = SignDemand.demands.get(NBTHelper.readResourceLocation(dataTag, "Demand"));
      if (dataTag.contains("DemandExtra"))
        demandExtra = dataTag.getCompound("DemandExtra");
    }

    public void write(CompoundTag posTag, CompoundTag dataTag) {
      posTag.merge(NbtUtils.writeBlockPos(pos));

      if (demand != null)
        NBTHelper.writeResourceLocation(dataTag, "Demand", demand.id);
      if (demandExtra != null)
        dataTag.put("DemandExtra", demandExtra);
    }
  }
}
