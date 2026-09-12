package purplecreate.tramways.content.signals.block;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import purplecreate.tramways.TTags;
import purplecreate.tramways.content.signals.SignalDisplaySource;
import purplecreate.tramways.content.signals.base.ExtendedSignalState;
import purplecreate.tramways.content.signals.base.JunctionState;
import purplecreate.tramways.content.signals.render.SignalType;
import purplecreate.tramways.content.signals.render.SignalTypes;
import purplecreate.tramways.mixinInterfaces.IRoutedSignal;

import java.util.*;

public class SignalBlockEntity extends SmartBlockEntity {
  @Nullable private DisplayLinkBlockEntity boundDisplayLink;
  
  private SignalType signalType;
  private ExtendedSignalState signalState = ExtendedSignalState.INVALID;
  @Nullable private JunctionState junctionState;
  @Nullable private BlockPos boundSignal;

  public SignalBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
  }

  @Override
  public void addBehaviours(List<BlockEntityBehaviour> list) {}

  public void bindDisplayLink(DisplayLinkBlockEntity displayLink) {
    boundDisplayLink = displayLink;
  }

  @Override
  public void tick() {
    super.tick();
    if (level == null || level.isClientSide) return;

    Optional<com.simibubi.create.content.trains.signal.SignalBlockEntity> optional = findTrackSignal();
    ExtendedSignalState nextSignalState;
    JunctionState nextJunctionState;

    if (optional.isEmpty()) {
      nextSignalState = ExtendedSignalState.INVALID;
      nextJunctionState = null;
    } else {
      var signal = optional.get();
      if (!(signal.getSignal() instanceof IRoutedSignal routedSignal)) return;

      nextSignalState = routedSignal.tramways$getExtendedState(signal.getBlockPos());
      nextJunctionState = routedSignal.tramways$getSelectedRoute(signal.getBlockPos());
    }

    if (!Objects.equals(nextSignalState, signalState) || !Objects.equals(nextJunctionState, junctionState)) {
      signalState = nextSignalState;
      junctionState = nextJunctionState;
      notifyUpdate();
    }
  }

  private Optional<com.simibubi.create.content.trains.signal.SignalBlockEntity> getTrackSignalAt(BlockPos pos) {
    if (level == null || pos == null) return Optional.empty();
    return level.getBlockEntity(pos, AllBlockEntityTypes.TRACK_SIGNAL.get());
  }

  private Optional<com.simibubi.create.content.trains.signal.SignalBlockEntity> findTrackSignal() {
    if (level == null || !(getBlockState().getBlock() instanceof SignalBlock block))
      return Optional.empty();

    if (boundDisplayLink != null && !boundDisplayLink.isRemoved()) {
      if (boundDisplayLink.activeSource instanceof SignalDisplaySource) {
        BlockPos pos = boundDisplayLink.getSourcePosition();
        BlockState state = level.getBlockState(pos);
        if (AllBlocks.TRACK_SIGNAL.has(state)) {
          boundSignal = pos;
          return getTrackSignalAt(pos);
        }
      }
    } else {
      boundDisplayLink = null;
    }

    BlockPos pos = block.getPole(getBlockState(), worldPosition);
    if (pos.equals(worldPosition)) {
      pos = pos.below();
    }

    for (int i = 0; i < 16; i++) {
      BlockState state = level.getBlockState(pos);
      if (AllBlocks.TRACK_SIGNAL.has(state)) {
        boundSignal = pos;
        return getTrackSignalAt(pos);
      } else if (!state.is(TTags.SIGNAL_POLE)) {
        break;
      }
      pos = pos.below();
    }

    boundSignal = null;
    return Optional.empty();
  }

  public ExtendedSignalState getSignalState() {
    return signalState;
  }

  @Nullable
  public JunctionState getJunctionState() {
    return junctionState;
  }

  @Override
  protected void read(CompoundTag tag, boolean clientPacket) {
    super.read(tag, clientPacket);

    signalType = SignalTypes.get(NBTHelper.readResourceLocation(tag, "SignalType"));
    boundSignal = tag.contains("BoundSignal", 10) ? NbtUtils.readBlockPos(tag.getCompound("BoundSignal")) : null;
    junctionState = tag.contains("JunctionState", 10) ? JunctionState.fromNbt(tag.getCompound("JunctionState")) : null;
    signalState = NBTHelper.readEnum(tag, "SignalState", ExtendedSignalState.class);
  }

  @Override
  protected void write(CompoundTag tag, boolean clientPacket) {
    super.write(tag, clientPacket);

    NBTHelper.writeResourceLocation(tag, "SignalType", signalType.getId());

    if (boundSignal != null) {
      tag.put("BoundSignal", NbtUtils.writeBlockPos(boundSignal));
    }

    if (junctionState != null) {
      tag.put("JunctionState", junctionState.toNbt());
    }

    NBTHelper.writeEnum(tag, "SignalState", signalState);
  }
}
