package purplecreate.tramways.content.requestStop.network;

import io.netty.buffer.ByteBuf;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import purplecreate.tramways.TNetworking;
import purplecreate.tramways.content.requestStop.RequestStopClient;
import purplecreate.tramways.util.Env;

public record StoppingBroadcastPacket(State state, float progress, String stationName) implements ClientboundPacketPayload {
  public static final StreamCodec<ByteBuf, StoppingBroadcastPacket> STREAM_CODEC = StreamCodec.composite(
    CatnipStreamCodecBuilders.ofEnum(State.class), StoppingBroadcastPacket::state,
    ByteBufCodecs.FLOAT, StoppingBroadcastPacket::progress,
    ByteBufCodecs.STRING_UTF8, StoppingBroadcastPacket::stationName,
    StoppingBroadcastPacket::new
  );

  public enum State {
    UPDATE_COUNTDOWN,
    SHOW_STOPPING,
    REMOVE
  }

  @Override
  public PacketTypeProvider getTypeProvider() {
    return TNetworking.C_STOPPING_BROADCAST;
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void handle(LocalPlayer player) {
    Env.unsafeRunWhenOn(Env.CLIENT, () -> () -> {
      RequestStopClient.handleBroadcast(Minecraft.getInstance(), this);
    });
  }
}
