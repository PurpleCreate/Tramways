package purplecreate.tramways.content.requestStop.network;

import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import purplecreate.tramways.TNetworking;
import purplecreate.tramways.content.requestStop.RequestStopServer;

public class RequestStopPacket implements ServerboundPacketPayload {
  public static final StreamCodec<ByteBuf, RequestStopPacket> STREAM_CODEC = StreamCodec.of(
    (buf, p) -> {},
    (buf) -> new RequestStopPacket()
  );

  @Override
  public PacketTypeProvider getTypeProvider() {
    return TNetworking.S_REQUEST_STOP;
  }

  @Override
  public void handle(ServerPlayer player) {
    if (player.getVehicle() instanceof CarriageContraptionEntity cce) {
      RequestStopServer.request(cce.getCarriage().train);
    }
  }
}
