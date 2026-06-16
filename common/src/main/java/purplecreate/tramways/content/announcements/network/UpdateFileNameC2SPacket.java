package purplecreate.tramways.content.announcements.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import purplecreate.tramways.content.announcements.engine.files.FileInfo;
import purplecreate.tramways.content.announcements.engine.files.ServerFileManager;
import purplecreate.tramways.util.C2SPacket;

public class UpdateFileNameC2SPacket implements C2SPacket {
  private final FileInfo info;
  private final String newName;

  public UpdateFileNameC2SPacket(FileInfo info, String newName) {
    this.info = info;
    this.newName = newName;
  }

  public static UpdateFileNameC2SPacket read(FriendlyByteBuf buf) {
    return new UpdateFileNameC2SPacket(
      FileInfo.fromNetwork(buf),
      buf.readUtf()
    );
  }

  @Override
  public void write(FriendlyByteBuf buf) {
    info.toNetwork(buf);
    buf.writeUtf(newName);
  }

  @Override
  public void handle(ServerPlayer player) {
    ServerFileManager.getInstance(player.server).renameFile(player, info, newName);
  }
}
