package purplecreate.tramways.content.announcements.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;
import purplecreate.tramways.content.announcements.engine.files.ClientFileManager;
import purplecreate.tramways.content.announcements.engine.files.ServerFileManager;
import purplecreate.tramways.content.announcements.engine.files.TransmissionStatus;
import purplecreate.tramways.util.BiPacket;

public class UpdateStatusBiPacket implements BiPacket {
  private final int id;
  private final TransmissionStatus status;
  private final Component details;

  public UpdateStatusBiPacket(int id, TransmissionStatus status, @Nullable Component details) {
    this.id = id;
    this.status = status;
    this.details = details == null ? Component.empty() : details;
  }

  public static UpdateStatusBiPacket read(FriendlyByteBuf buf) {
    return new UpdateStatusBiPacket(
      buf.readVarInt(),
      buf.readEnum(TransmissionStatus.class),
      buf.readComponent()
    );
  }

  @Override
  public void write(FriendlyByteBuf buf) {
    buf.writeVarInt(id);
    buf.writeEnum(status);
    buf.writeComponent(details);
  }

  @Override
  public void handleOnClient(Minecraft mc) {
    ClientFileManager.getInstance().setTransmissionStatus(null, id, status, details);
  }

  @Override
  public void handleOnServer(ServerPlayer player) {
    ServerFileManager.getInstance(player.server).setTransmissionStatus(player, id, status, details);
  }
}
