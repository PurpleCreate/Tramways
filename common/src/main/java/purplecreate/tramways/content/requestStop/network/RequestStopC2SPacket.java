package purplecreate.tramways.content.requestStop.network;

import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import purplecreate.tramways.content.requestStop.RequestStopServer;
import purplecreate.tramways.util.C2SPacket;

public class RequestStopC2SPacket implements C2SPacket {
  public static RequestStopC2SPacket read(FriendlyByteBuf buf) {
    return new RequestStopC2SPacket();
  }

  @Override
  public void write(FriendlyByteBuf buf) {

  }

  @Override
  public void handle(ServerPlayer player) {
    if (player.getVehicle() instanceof CarriageContraptionEntity cce) {
      RequestStopServer.request(cce.getCarriage().train);
    }
  }
}
