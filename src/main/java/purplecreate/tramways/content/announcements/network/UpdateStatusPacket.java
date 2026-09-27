package purplecreate.tramways.content.announcements.network;

import com.mojang.datafixers.util.Function3;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;
import purplecreate.tramways.TNetworking;
import purplecreate.tramways.content.announcements.engine.files.ClientFileManager;
import purplecreate.tramways.content.announcements.engine.files.ServerFileManager;
import purplecreate.tramways.content.announcements.engine.files.TransmissionStatus;

public class UpdateStatusPacket implements BidirectionalPacket<UpdateStatusPacket.Clientbound, UpdateStatusPacket.Serverbound> {
  protected final int id;
  protected final TransmissionStatus status;
  @Nullable protected final Component details;

  public UpdateStatusPacket(int id, TransmissionStatus status, @Nullable Component details) {
    this.id = id;
    this.status = status;
    this.details = details;
  }

  protected static <T extends UpdateStatusPacket> StreamCodec<RegistryFriendlyByteBuf, T> codec(Function3<Integer, TransmissionStatus, Component, T> factory) {
    return StreamCodec.composite(
      ByteBufCodecs.VAR_INT, p -> p.id,
      CatnipStreamCodecBuilders.ofEnum(TransmissionStatus.class), p -> p.status,
      CatnipStreamCodecBuilders.nullable(ComponentSerialization.STREAM_CODEC), p -> p.details,
      factory
    );
  }

  @Override
  public Clientbound clientbound() {
    return new Clientbound(id, status, details);
  }

  @Override
  public Serverbound serverbound() {
    return new Serverbound(id, status, details);
  }

  public static class Clientbound extends UpdateStatusPacket implements ClientboundPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, Clientbound> STREAM_CODEC = codec(Clientbound::new);

    public Clientbound(int id, TransmissionStatus status, @Nullable Component details) {
      super(id, status, details);
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
      return TNetworking.C_UPDATE_STATUS;
    }

    @Override
    public void handle(LocalPlayer player) {
      ClientFileManager.getInstance().setTransmissionStatus(null, id, status, details);
    }
  }

  public static class Serverbound extends UpdateStatusPacket implements ServerboundPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, Serverbound> STREAM_CODEC = codec(Serverbound::new);

    public Serverbound(int id, TransmissionStatus status, @Nullable Component details) {
      super(id, status, details);
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
      return TNetworking.S_UPDATE_STATUS;
    }

    @Override
    public void handle(ServerPlayer player) {
      ServerFileManager.getInstance(player.server).setTransmissionStatus(player, id, status, details);
    }
  }
}
