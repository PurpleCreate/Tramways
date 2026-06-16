package purplecreate.tramways.content.announcements.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import purplecreate.tramways.content.announcements.engine.files.FileInfo;
import purplecreate.tramways.content.announcements.engine.files.ServerFileManager;
import purplecreate.tramways.util.C2SPacket;

import java.util.HashSet;
import java.util.Set;

public class RequestFilesC2SPacket implements C2SPacket {
  private final Set<String> fileNames;

  public RequestFilesC2SPacket(Set<String> fileNames) {
    this.fileNames = fileNames;
  }

  public static RequestFilesC2SPacket read(FriendlyByteBuf buf) {
    Set<String> fileNames = new HashSet<>();
    int length = buf.readVarInt();
    for (int i = 0; i < length; i++) {
      fileNames.add(buf.readUtf().toLowerCase());
    }
    return new RequestFilesC2SPacket(fileNames);
  }

  @Override
  public void write(FriendlyByteBuf buf) {
    buf.writeVarInt(fileNames.size());
    for (String name : fileNames) {
      buf.writeUtf(name.toLowerCase());
    }
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
