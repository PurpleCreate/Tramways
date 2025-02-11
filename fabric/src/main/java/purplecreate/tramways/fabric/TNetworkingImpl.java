package purplecreate.tramways.fabric;

import io.github.fabricators_of_create.porting_lib.util.ServerLifecycleHooks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import purplecreate.tramways.TNetworking;
import purplecreate.tramways.Tramways;
import purplecreate.tramways.util.C2SPacket;
import purplecreate.tramways.util.S2CPacket;

public class TNetworkingImpl {
  private static final ResourceLocation fabricChannel = Tramways.rl("net");

  @Environment(EnvType.CLIENT)
  public static void clientInit() {
    ClientPlayNetworking.registerGlobalReceiver(fabricChannel, (mc, listener, buf, sender) ->
      TNetworking.handleInternal(buf, mc)
    );
  }

  @Environment(EnvType.SERVER)
  public static void serverInit() {
    ServerPlayNetworking.registerGlobalReceiver(fabricChannel, (server, player, listener, buf, sender) ->
      TNetworking.handleInternal(buf, player)
    );
  }

  public static <T extends S2CPacket> void sendToAll(T message) {
    TNetworking.sendInternal(message, (buf) ->
      PlayerLookup.all(ServerLifecycleHooks.getCurrentServer())
        .forEach((player) -> ServerPlayNetworking.send(player, fabricChannel, buf))
    );
  }

  public static <T extends S2CPacket> void sendToNear(T message, Vec3 pos, int range, ResourceKey<Level> dimension) {
    TNetworking.sendInternal(message, (buf) ->
      PlayerLookup.around(ServerLifecycleHooks.getCurrentServer().getLevel(dimension), pos, range)
        .forEach((player) -> ServerPlayNetworking.send(player, fabricChannel, buf))
    );
  }

  public static <T extends S2CPacket> void sendToPlayer(T message, ServerPlayer player) {
    TNetworking.sendInternal(message, (buf) ->
      ServerPlayNetworking.send(player, fabricChannel, buf)
    );
  }

  public static <T extends C2SPacket> void sendToServer(T message) {
    TNetworking.sendInternal(message, (buf) ->
      ClientPlayNetworking.send(fabricChannel, buf)
    );
  }
}
