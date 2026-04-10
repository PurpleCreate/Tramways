package purplecreate.tramways.content.signals.routing;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.content.trains.signal.SignalBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import purplecreate.tramways.content.signals.base.JunctionState;
import purplecreate.tramways.mixinInterfaces.IRoutedSignal;
import purplecreate.tramways.util.C2SPacket;

import java.util.Objects;

public class UpdateSignalC2SPacket implements C2SPacket {
  BlockPos pos;
  JunctionState junctionState;

  public UpdateSignalC2SPacket(BlockPos pos, JunctionState junctionState) {
    this.pos = pos;
    this.junctionState = junctionState;
  }

  public static UpdateSignalC2SPacket removeRoute(BlockPos pos) {
    return new UpdateSignalC2SPacket(pos, null);
  }

  public static UpdateSignalC2SPacket read(FriendlyByteBuf buf) {
    BlockPos pos = buf.readBlockPos();
    CompoundTag junctionState = buf.readNbt();
    Objects.requireNonNull(junctionState);
    return new UpdateSignalC2SPacket(pos, JunctionState.fromNbt(junctionState));
  }

  @Override
  public void write(FriendlyByteBuf buf) {
    buf.writeBlockPos(pos);
    buf.writeNbt(junctionState == null ? new CompoundTag() : junctionState.toNbt());
  }

  @Override
  public void handle(ServerPlayer player) {
    if (player == null) return;
    Level level = player.level();
    if (!level.isLoaded(pos)) return;
    if (!pos.closerThan(player.blockPosition(), 20)) return;

    SignalBlockEntity be = level.getBlockEntity(pos, AllBlockEntityTypes.TRACK_SIGNAL.get()).orElse(null);
    if (be == null || !(be.getSignal() instanceof IRoutedSignal signal)) return;

    signal.tramways$assignRoute(pos, junctionState);
  }
}
