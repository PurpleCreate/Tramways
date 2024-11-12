package purplecreate.tramways.content.requestStop.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import purplecreate.tramways.content.requestStop.RequestStopClient;
import purplecreate.tramways.util.Env;
import purplecreate.tramways.util.S2CPacket;

public class StoppingBroadcastS2CPacket implements S2CPacket {
  public enum Type {
    UPDATE_COUNTDOWN,
    SHOW_STOPPING,
    REMOVE
  }

  public final Type type;
  public final float progress;
  public final String stationName;

  public StoppingBroadcastS2CPacket(Type type, float progress, String stationName) {
    this.type = type;
    this.progress = progress;
    this.stationName = stationName;
  }

  public static StoppingBroadcastS2CPacket read(FriendlyByteBuf buf) {
    int typeOrdinal = buf.readVarInt();
    float progress = buf.readFloat();
    String stationName = buf.readUtf();

    for (Type type : Type.class.getEnumConstants()) {
      if (type.ordinal() == typeOrdinal) {
        return new StoppingBroadcastS2CPacket(type, progress, stationName);
      }
    }

    return new StoppingBroadcastS2CPacket(Type.REMOVE, progress, stationName);
  }

  @Override
  public void write(FriendlyByteBuf buf) {
    buf.writeVarInt(type.ordinal());
    buf.writeFloat(progress);
    buf.writeUtf(stationName);
  }

  @Override
  @Environment(EnvType.CLIENT)
  public void handle(Minecraft mc) {
    Env.unsafeRunWhenOn(Env.CLIENT, () -> () -> {
      RequestStopClient.handleBroadcast(mc, this);
    });
  }
}
