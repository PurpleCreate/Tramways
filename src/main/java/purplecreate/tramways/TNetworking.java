package purplecreate.tramways;

import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.CatnipPacketRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import purplecreate.tramways.content.announcements.network.*;
import purplecreate.tramways.content.requestStop.network.RequestStopPacket;
import purplecreate.tramways.content.requestStop.network.StoppingBroadcastPacket;
import purplecreate.tramways.content.signs.network.SaveSignSettingsPacket;
import purplecreate.tramways.content.stationDeco.nameSign.network.UpdateNameSignPacket;
import purplecreate.tramways.util.Env;

public enum TNetworking implements BasePacketPayload.PacketTypeProvider {
  // tram/railway signs
  S_SAVE_SIGN_SETTINGS(SaveSignSettingsPacket.class, SaveSignSettingsPacket.STREAM_CODEC),

  // request stop
  S_REQUEST_STOP(RequestStopPacket.class, RequestStopPacket.STREAM_CODEC),
  C_STOPPING_BROADCAST(StoppingBroadcastPacket.class, StoppingBroadcastPacket.STREAM_CODEC),

  // name sign
  S_UPDATE_NAME_SIGN(UpdateNameSignPacket.class, UpdateNameSignPacket.STREAM_CODEC),

  // announcements
  C_START_TRANSMITTING(StartTransmittingPacket.Clientbound.class, StartTransmittingPacket.Clientbound.STREAM_CODEC),
  S_START_TRANSMITTING(StartTransmittingPacket.Serverbound.class, StartTransmittingPacket.Serverbound.STREAM_CODEC),
  C_UPDATE_STATUS(UpdateStatusPacket.Clientbound.class, UpdateStatusPacket.Clientbound.STREAM_CODEC),
  S_UPDATE_STATUS(UpdateStatusPacket.Serverbound.class, UpdateStatusPacket.Serverbound.STREAM_CODEC),
  C_TRANSMIT_CHUNK(TransmitChunkPacket.Clientbound.class, TransmitChunkPacket.Clientbound.STREAM_CODEC),
  S_TRANSMIT_CHUNK(TransmitChunkPacket.Serverbound.class, TransmitChunkPacket.Serverbound.STREAM_CODEC),
  C_DATABASE_SYNC(DatabaseSyncPacket.class, DatabaseSyncPacket.STREAM_CODEC),
  S_REQUEST_FILES(RequestFilesPacket.class, RequestFilesPacket.STREAM_CODEC),
  S_UPDATE_FILE_NAME(UpdateFileNamePacket.class, UpdateFileNamePacket.STREAM_CODEC),
  S_DELETE_FILE(DeleteFilePacket.class, DeleteFilePacket.STREAM_CODEC),
  C_STATION_ANNOUNCEMENT_EVENT(StationAnnouncementEventPacket.class, StationAnnouncementEventPacket.STREAM_CODEC),
  C_TRAIN_ANNOUNCEMENT_EVENT(TrainAnnouncementEventPacket.class, TrainAnnouncementEventPacket.STREAM_CODEC);

  private final CatnipPacketRegistry.PacketType<?> type;

  <T extends BasePacketPayload> TNetworking(Class<T> clazz, StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
    String name = this.name().toLowerCase();
    type = new CatnipPacketRegistry.PacketType<>(new CustomPacketPayload.Type<>(Tramways.rl(name)), clazz, codec);
  }

  @Override
  public <T extends CustomPacketPayload> CustomPacketPayload.Type<T> getType() {
    return (CustomPacketPayload.Type<T>) this.type.type();
  }

  public static void register() {
    CatnipPacketRegistry packetRegistry = new CatnipPacketRegistry(Tramways.ID, "neo+" + Env.getVersion());
    for (TNetworking packet : TNetworking.values()) {
      packetRegistry.registerPacket(packet.type);
    }
    packetRegistry.registerAllPackets();
  }
}
