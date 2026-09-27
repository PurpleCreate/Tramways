package purplecreate.tramways.content.announcements.network;

import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import purplecreate.tramways.TNetworking;
import purplecreate.tramways.content.announcements.engine.files.FileInfo;
import purplecreate.tramways.content.announcements.engine.files.ServerFileManager;
import purplecreate.tramways.util.TramwaysCodecBuilders;

import java.util.Set;

public record RequestFilesPacket(Set<String> fileNames) implements ServerboundPacketPayload {
  public static final StreamCodec<ByteBuf, RequestFilesPacket> STREAM_CODEC = StreamCodec.composite(
    TramwaysCodecBuilders.set(ByteBufCodecs.STRING_UTF8), RequestFilesPacket::fileNames,
    RequestFilesPacket::new
  );

  @Override
  public PacketTypeProvider getTypeProvider() {
    return TNetworking.S_REQUEST_FILES;
  }

  @Override
  public void handle(ServerPlayer player) {
    ServerFileManager fm = ServerFileManager.getInstance(player.server);

    for (FileInfo info : fm.getFiles()) {
      if (!fileNames.contains(info.name().toLowerCase())) continue;
      fm.startTransmittingFromDatabase(player, info);
    }
  }
}
