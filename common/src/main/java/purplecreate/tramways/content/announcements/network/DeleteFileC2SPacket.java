package purplecreate.tramways.content.announcements.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import purplecreate.tramways.content.announcements.engine.files.FileInfo;
import purplecreate.tramways.content.announcements.engine.files.ServerFileManager;
import purplecreate.tramways.util.C2SPacket;

public class DeleteFileC2SPacket implements C2SPacket {
  private final FileInfo file;

  public DeleteFileC2SPacket(FileInfo file) {
    this.file = file;
  }

  public static DeleteFileC2SPacket read(FriendlyByteBuf buf) {
    return new DeleteFileC2SPacket(FileInfo.fromNetwork(buf));
  }

  @Override
  public void write(FriendlyByteBuf buf) {
    file.toNetwork(buf);
  }

  @Override
  public void handle(ServerPlayer player) {
    ServerFileManager.getInstance(player.server).deleteFile(player, file);
  }
}
