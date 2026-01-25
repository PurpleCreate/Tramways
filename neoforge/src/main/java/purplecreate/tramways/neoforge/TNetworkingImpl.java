package purplecreate.tramways.neoforge;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import purplecreate.tramways.TNetworking;
import purplecreate.tramways.Tramways;
import purplecreate.tramways.util.C2SPacket;
import purplecreate.tramways.util.S2CPacket;

import java.util.Objects;

public class TNetworkingImpl {
  private record NeoForgePacket(FriendlyByteBuf message) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<NeoForgePacket> TYPE = new CustomPacketPayload.Type<>(Tramways.rl("packet"));
    public static final StreamCodec<FriendlyByteBuf, NeoForgePacket> CODEC = StreamCodec.of(
      NeoForgePacket::write,
      NeoForgePacket::read
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
      return TYPE;
    }

    public static NeoForgePacket read(FriendlyByteBuf buf) {
      int length = buf.readVarInt();
      FriendlyByteBuf message = new FriendlyByteBuf(buf.readBytes(length));
      return new NeoForgePacket(message);
    }

    public static void write(FriendlyByteBuf buf, NeoForgePacket packet) {
      byte[] array = packet.message.array();
      buf.writeVarInt(array.length);
      buf.writeBytes(array);
    }

    public void handle(IPayloadContext context) {
      switch (context.flow()) {
        case CLIENTBOUND -> TNetworking.handleInternal(message, Minecraft.getInstance());
        case SERVERBOUND -> TNetworking.handleInternal(message, (ServerPlayer)context.player());
      }
    }
  }

  public static void init() {
    IEventBus modEventBus = ModLoadingContext.get().getActiveContainer().getEventBus();

    modEventBus.addListener((RegisterPayloadHandlersEvent event) -> {
      PayloadRegistrar registrar = event.registrar("0");
      registrar.playBidirectional(
        NeoForgePacket.TYPE,
        NeoForgePacket.CODEC,
        NeoForgePacket::handle
      );
    });
  }

  public static <T extends S2CPacket> void sendToAll(T message) {
    TNetworking.sendInternal(message, (buf) ->
      PacketDistributor.sendToAllPlayers(new NeoForgePacket(buf))
    );
  }

  public static <T extends S2CPacket> void sendToNear(T message, Vec3 pos, int range, ResourceKey<Level> dimension) {
    TNetworking.sendInternal(message, (buf) ->
      Objects.requireNonNull(ServerLifecycleHooks.getCurrentServer())
        .getPlayerList()
        .broadcast(
          null,
          pos.x,
          pos.y,
          pos.z,
          range,
          dimension,
          new ClientboundCustomPayloadPacket(new NeoForgePacket(buf))
        )
    );
  }

  public static <T extends S2CPacket> void sendToPlayer(T message, ServerPlayer player) {
    TNetworking.sendInternal(message, (buf) ->
      PacketDistributor.sendToPlayer(player, new NeoForgePacket(buf))
    );
  }

  public static <T extends C2SPacket> void sendToServer(T message) {
    TNetworking.sendInternal(message, (buf) ->
      PacketDistributor.sendToServer(new NeoForgePacket(buf))
    );
  }
}
