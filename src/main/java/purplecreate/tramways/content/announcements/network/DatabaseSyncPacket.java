package purplecreate.tramways.content.announcements.network;

import io.netty.buffer.ByteBuf;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.codec.StreamCodec;
import purplecreate.tramways.TNetworking;
import purplecreate.tramways.content.announcements.engine.files.FileInfo;
import purplecreate.tramways.content.announcements.engine.files.ClientFileManager;

import java.util.List;

public record DatabaseSyncPacket(List<FileInfo> files) implements ClientboundPacketPayload {
  public static final StreamCodec<ByteBuf, DatabaseSyncPacket> STREAM_CODEC = StreamCodec.composite(
    CatnipStreamCodecBuilders.list(FileInfo.STREAM_CODEC), DatabaseSyncPacket::files,
    DatabaseSyncPacket::new
  );

  @Override
  public PacketTypeProvider getTypeProvider() {
    return TNetworking.C_DATABASE_SYNC;
  }

  @Override
  public void handle(LocalPlayer mc) {
    ClientFileManager.getInstance().handleSyncPacket(files);
  }
}
