package purplecreate.tramways.util;

import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;

public interface BiPacket extends C2SPacket, S2CPacket {
  @Override
  default void handle(Minecraft mc) {
    handleOnClient(mc);
  }

  void handleOnClient(Minecraft mc);

  @Override
  default void handle(ServerPlayer player) {
    handleOnServer(player);
  }

  void handleOnServer(ServerPlayer player);
}
