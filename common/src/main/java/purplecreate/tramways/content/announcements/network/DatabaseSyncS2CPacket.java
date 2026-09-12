package purplecreate.tramways.content.announcements.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import purplecreate.tramways.content.announcements.engine.files.FileInfo;
import purplecreate.tramways.content.announcements.engine.files.ClientFileManager;
import purplecreate.tramways.util.S2CPacket;

import java.util.ArrayList;
import java.util.List;

public class DatabaseSyncS2CPacket implements S2CPacket {
  private final List<FileInfo> files;

  public DatabaseSyncS2CPacket(List<FileInfo> files) {
    this.files = files;
  }

  public static DatabaseSyncS2CPacket read(FriendlyByteBuf buf) {
    List<FileInfo> files = new ArrayList<>();
    int length = buf.readVarInt();
    for (int i = 0; i < length; i++) {
      files.add(FileInfo.fromNetwork(buf));
    }
    return new DatabaseSyncS2CPacket(files);
  }

  @Override
  public void write(FriendlyByteBuf buf) {
    buf.writeVarInt(files.size());
    for (FileInfo info : files) {
      info.toNetwork(buf);
    }
  }

  @Override
  public void handle(Minecraft mc) {
    ClientFileManager.getInstance().handleSyncPacket(files);
  }
}
