package purplecreate.tramways.content.announcements.network;

import com.mojang.datafixers.util.Function3;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import purplecreate.tramways.TNetworking;
import purplecreate.tramways.content.announcements.engine.files.ClientFileManager;
import purplecreate.tramways.content.announcements.engine.files.ServerFileManager;

public class TransmitChunkPacket implements BidirectionalPacket<TransmitChunkPacket.Clientbound, TransmitChunkPacket.Serverbound> {
  protected final int id;
  protected final int offset;
  protected final byte[] chunk;

  public TransmitChunkPacket(int id, int offset, byte[] chunk) {
    this.id = id;
    this.offset = offset;
    this.chunk = chunk;
  }

  protected static <T extends TransmitChunkPacket> StreamCodec<ByteBuf, T> codec(Function3<Integer, Integer, byte[], T> factory) {
    return StreamCodec.composite(
      ByteBufCodecs.VAR_INT, p -> p.id,
      ByteBufCodecs.VAR_INT, p -> p.offset,
      ByteBufCodecs.BYTE_ARRAY, p -> p.chunk,
      factory
    );
  }

  @Override
  public Clientbound clientbound() {
    return new Clientbound(id, offset, chunk);
  }

  @Override
  public Serverbound serverbound() {
    return new Serverbound(id, offset, chunk);
  }

  public static class Clientbound extends TransmitChunkPacket implements ClientboundPacketPayload {
    public static final StreamCodec<ByteBuf, Clientbound> STREAM_CODEC = codec(Clientbound::new);

    public Clientbound(int id, int offset, byte[] chunk) {
      super(id, offset, chunk);
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
      return TNetworking.C_TRANSMIT_CHUNK;
    }

    @Override
    public void handle(LocalPlayer player) {
      ClientFileManager.getInstance().receive(null, id, offset, chunk);
    }
  }

  public static class Serverbound extends TransmitChunkPacket implements ServerboundPacketPayload {
    public static final StreamCodec<ByteBuf, Serverbound> STREAM_CODEC = codec(Serverbound::new);

    public Serverbound(int id, int offset, byte[] chunk) {
      super(id, offset, chunk);
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
      return TNetworking.S_TRANSMIT_CHUNK;
    }

    @Override
    public void handle(ServerPlayer player) {
      ServerFileManager.getInstance(player.server).receive(player, id, offset, chunk);
    }
  }
}
