package purplecreate.tramways.content.announcements.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import purplecreate.tramways.content.announcements.engine.files.ClientFileManager;
import purplecreate.tramways.content.announcements.engine.files.ServerFileManager;
import purplecreate.tramways.util.BiPacket;

public class TransmitChunkBiPacket implements BiPacket {
  private final int id;
  private final int offset;
  private final byte[] chunk;

  public TransmitChunkBiPacket(int id, int offset, byte[] chunk) {
    this.id = id;
    this.offset = offset;
    this.chunk = chunk;
  }

  public static TransmitChunkBiPacket read(FriendlyByteBuf buf) {
    return new TransmitChunkBiPacket(
      buf.readVarInt(),
      buf.readVarInt(),
      buf.readByteArray()
    );
  }

  @Override
  public void write(FriendlyByteBuf buf) {
    buf.writeVarInt(id);
    buf.writeVarInt(offset);
    buf.writeByteArray(chunk);
  }

  @Override
  public void handleOnClient(Minecraft mc) {
    ClientFileManager.getInstance().receive(null, id, offset, chunk);
  }

  @Override
  public void handleOnServer(ServerPlayer player) {
    ServerFileManager.getInstance(player.server).receive(player, id, offset, chunk);
  }
}
