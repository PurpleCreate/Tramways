package purplecreate.tramways.content.announcements.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import purplecreate.tramways.content.announcements.engine.files.FileInfo;
import purplecreate.tramways.content.announcements.engine.files.ClientFileManager;
import purplecreate.tramways.content.announcements.engine.files.ServerFileManager;
import purplecreate.tramways.util.BiPacket;

public class StartTransmittingBiPacket implements BiPacket {
  private final int id;
  private final FileInfo info;
  private final boolean adminMode;

  public StartTransmittingBiPacket(int id, FileInfo info, boolean adminMode) {
    this.id = id;
    this.info = info;
    this.adminMode = adminMode;
  }

  public static StartTransmittingBiPacket read(FriendlyByteBuf buf) {
    return new StartTransmittingBiPacket(
      buf.readVarInt(),
      FileInfo.fromNetwork(buf),
      buf.readBoolean()
    );
  }

  @Override
  public void write(FriendlyByteBuf buf) {
    buf.writeVarInt(id);
    info.toNetwork(buf);
    buf.writeBoolean(adminMode);
  }

  @Override
  public void handleOnClient(Minecraft mc) {
    ClientFileManager.getInstance().startReceiving(null, info, id, adminMode);
  }

  @Override
  public void handleOnServer(ServerPlayer player) {
    ServerFileManager.getInstance(player.server).startReceiving(player, info, id, adminMode);
  }
}
