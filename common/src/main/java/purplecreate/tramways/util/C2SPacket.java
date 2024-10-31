package purplecreate.tramways.util;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public interface C2SPacket {
  void write(FriendlyByteBuf buf);
  void handle(ServerPlayer player);
}
