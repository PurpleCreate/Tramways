package purplecreate.tramways.content.announcements.network;

import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import purplecreate.tramways.TNetworking;
import purplecreate.tramways.content.announcements.engine.files.FileInfo;
import purplecreate.tramways.content.announcements.engine.files.ServerFileManager;

public record UpdateFileNamePacket(FileInfo info, String newName) implements ServerboundPacketPayload {
  public static final StreamCodec<ByteBuf, UpdateFileNamePacket> STREAM_CODEC = StreamCodec.composite(
    FileInfo.STREAM_CODEC, UpdateFileNamePacket::info,
    ByteBufCodecs.STRING_UTF8, UpdateFileNamePacket::newName,
    UpdateFileNamePacket::new
  );

  @Override
  public PacketTypeProvider getTypeProvider() {
    return TNetworking.S_UPDATE_FILE_NAME;
  }

  @Override
  public void handle(ServerPlayer player) {
    ServerFileManager.getInstance(player.server).renameFile(player, info, newName);
  }
}
