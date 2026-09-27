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
import purplecreate.tramways.content.announcements.engine.files.FileInfo;
import purplecreate.tramways.content.announcements.engine.files.ClientFileManager;
import purplecreate.tramways.content.announcements.engine.files.ServerFileManager;

public class StartTransmittingPacket implements BidirectionalPacket<StartTransmittingPacket.Clientbound, StartTransmittingPacket.Serverbound> {
  protected final int id;
  protected final FileInfo info;
  protected final boolean adminMode;

  public StartTransmittingPacket(int id, FileInfo info, boolean adminMode) {
    this.id = id;
    this.info = info;
    this.adminMode = adminMode;
  }

  protected static <T extends StartTransmittingPacket> StreamCodec<ByteBuf, T> codec(Function3<Integer, FileInfo, Boolean, T> factory) {
    return StreamCodec.composite(
      ByteBufCodecs.VAR_INT, p -> p.id,
      FileInfo.STREAM_CODEC, p -> p.info,
      ByteBufCodecs.BOOL, p -> p.adminMode,
      factory
    );
  }

  @Override
  public Clientbound clientbound() {
    return new Clientbound(id, info, adminMode);
  }

  @Override
  public Serverbound serverbound() {
    return new Serverbound(id, info, adminMode);
  }

  public static class Clientbound extends StartTransmittingPacket implements ClientboundPacketPayload {
    public static final StreamCodec<ByteBuf, Clientbound> STREAM_CODEC = codec(Clientbound::new);

    public Clientbound(int id, FileInfo info, boolean adminMode) {
      super(id, info, adminMode);
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
      return TNetworking.C_START_TRANSMITTING;
    }

    @Override
    public void handle(LocalPlayer player) {
      ClientFileManager.getInstance().startReceiving(null, info, id, adminMode);
    }
  }

  public static class Serverbound extends StartTransmittingPacket implements ServerboundPacketPayload {
    public static final StreamCodec<ByteBuf, Serverbound> STREAM_CODEC = codec(Serverbound::new);

    public Serverbound(int id, FileInfo info, boolean adminMode) {
      super(id, info, adminMode);
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
      return TNetworking.S_START_TRANSMITTING;
    }

    @Override
    public void handle(ServerPlayer player) {
      ServerFileManager.getInstance(player.server).startReceiving(player, info, id, adminMode);
    }
  }
}
