package purplecreate.tramways.content.announcements.network;

import de.mrjulsen.crn.data.train.portable.StationDisplayData;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import purplecreate.tramways.TNetworking;
import purplecreate.tramways.content.announcements.config.AnnouncementVariant;
import purplecreate.tramways.content.announcements.engine.SoundEngine;

public record StationAnnouncementEventPacket(BlockPos speakerPos, AnnouncementVariant variant, StationDisplayData data) implements ClientboundPacketPayload {
  public static final StreamCodec<ByteBuf, StationAnnouncementEventPacket> STREAM_CODEC = StreamCodec.composite(
    BlockPos.STREAM_CODEC, StationAnnouncementEventPacket::speakerPos,
    ByteBufCodecs.COMPOUND_TAG, p -> p.variant.toNbt(),
    ByteBufCodecs.COMPOUND_TAG, p -> p.data.toNbt(),
    StationAnnouncementEventPacket::deserializeNbt
  );

  private static StationAnnouncementEventPacket deserializeNbt(BlockPos speakerPos, CompoundTag variant, CompoundTag data) {
    return new StationAnnouncementEventPacket(speakerPos, AnnouncementVariant.fromNbt(variant), StationDisplayData.fromNbt(data));
  }

  @Override
  public PacketTypeProvider getTypeProvider() {
    return TNetworking.C_STATION_ANNOUNCEMENT_EVENT;
  }

  @Override
  public void handle(LocalPlayer mc) {
    SoundEngine.enqueue(speakerPos, variant, data);
  }
}
