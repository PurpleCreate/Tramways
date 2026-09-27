package purplecreate.tramways.content.announcements.network;

import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import purplecreate.tramways.TNetworking;
import purplecreate.tramways.content.announcements.engine.files.FileInfo;
import purplecreate.tramways.content.announcements.engine.files.ServerFileManager;

public record DeleteFilePacket(FileInfo file) implements ServerboundPacketPayload {
  public static final StreamCodec<ByteBuf, DeleteFilePacket> STREAM_CODEC = StreamCodec.composite(
    FileInfo.STREAM_CODEC, DeleteFilePacket::file,
    DeleteFilePacket::new
  );

  @Override
  public PacketTypeProvider getTypeProvider() {
    return TNetworking.S_DELETE_FILE;
  }

  @Override
  public void handle(ServerPlayer player) {
    ServerFileManager.getInstance(player.server).deleteFile(player, file);
  }
}
