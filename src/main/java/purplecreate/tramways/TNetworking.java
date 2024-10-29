package purplecreate.tramways;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import purplecreate.tramways.content.announcements.network.PlayVoiceS2CPacket;
import purplecreate.tramways.content.announcements.network.PlayMovingVoiceS2CPacket;
import purplecreate.tramways.content.signs.network.SaveSignSettingsC2SPacket;

public class TNetworking {
  private static final String VERSION = "1";

  private static int id = 0;
  private static final SimpleChannel net =
    NetworkRegistry.ChannelBuilder
      .named(Tramways.rl("net"))
      .networkProtocolVersion(() -> VERSION)
      .clientAcceptedVersions(VERSION::equals)
      .serverAcceptedVersions(VERSION::equals)
      .simpleChannel();

  public static <MSG> void sendToAll(MSG message) {
    net.send(PacketDistributor.ALL.noArg(), message);
  }

  public static <MSG> void sendToNear(MSG message, Vec3 pos, int range, ResourceKey<Level> dimension) {
    sendToNear(message, pos.x, pos.y, pos.z, range, dimension);
  }

  public static <MSG> void sendToNear(MSG message, double x, double y, double z, int range, ResourceKey<Level> dimension) {
    net.send(
      PacketDistributor.NEAR.with(
        PacketDistributor.TargetPoint.p(x, y, z, range, dimension)
      ),
      message
    );
  }

  public static <MSG> void sendToServer(MSG message) {
    net.sendToServer(message);
  }

  public static void register() {
    net
      .messageBuilder(PlayVoiceS2CPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
      .encoder(PlayVoiceS2CPacket::write)
      .decoder(PlayVoiceS2CPacket::read)
      .consumerMainThread(PlayVoiceS2CPacket::handle)
      .add();

    net
      .messageBuilder(PlayMovingVoiceS2CPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
      .encoder(PlayMovingVoiceS2CPacket::write)
      .decoder(PlayMovingVoiceS2CPacket::read)
      .consumerMainThread(PlayMovingVoiceS2CPacket::handle)
      .add();

    net
      .messageBuilder(SaveSignSettingsC2SPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
      .encoder(SaveSignSettingsC2SPacket::write)
      .decoder(SaveSignSettingsC2SPacket::read)
      .consumerMainThread(SaveSignSettingsC2SPacket::handle)
      .add();
  }
}
