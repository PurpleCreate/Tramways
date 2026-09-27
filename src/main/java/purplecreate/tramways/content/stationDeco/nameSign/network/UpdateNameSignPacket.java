package purplecreate.tramways.content.stationDeco.nameSign.network;

import io.netty.buffer.ByteBuf;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import purplecreate.tramways.TNetworking;
import purplecreate.tramways.content.stationDeco.nameSign.NameSignBlockEntity;

import java.util.List;

public record UpdateNameSignPacket(BlockPos pos, List<String> lines) implements ServerboundPacketPayload {
  public static final StreamCodec<ByteBuf, UpdateNameSignPacket> STREAM_CODEC = StreamCodec.composite(
    BlockPos.STREAM_CODEC, UpdateNameSignPacket::pos,
    CatnipStreamCodecBuilders.list(ByteBufCodecs.STRING_UTF8), UpdateNameSignPacket::lines,
    UpdateNameSignPacket::new
  );

  @Override
  public PacketTypeProvider getTypeProvider() {
    return TNetworking.S_UPDATE_NAME_SIGN;
  }

  @Override
  public void handle(ServerPlayer player) {
    if (player == null) return;
    Level level = player.level();
    if (!level.isLoaded(pos)) return;
    if (!pos.closerThan(player.blockPosition(), 20)) return;

    if (level.getBlockEntity(pos) instanceof NameSignBlockEntity be) {
      be.lines = lines;
      be.notifyUpdate();
    }
  }
}
