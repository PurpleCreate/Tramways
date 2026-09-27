package purplecreate.tramways.content.announcements.network;

import com.simibubi.create.Create;
import com.simibubi.create.content.trains.entity.Train;
import de.mrjulsen.crn.data.train.portable.TrainDisplayData;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import purplecreate.tramways.TNetworking;
import purplecreate.tramways.content.announcements.config.AnnouncementVariant;
import purplecreate.tramways.content.announcements.engine.SoundEngine;

import java.util.UUID;

public record TrainAnnouncementEventPacket(UUID trainId, AnnouncementVariant variant, TrainDisplayData data) implements ClientboundPacketPayload {
  public static final StreamCodec<ByteBuf, TrainAnnouncementEventPacket> STREAM_CODEC = StreamCodec.composite(
    UUIDUtil.STREAM_CODEC, TrainAnnouncementEventPacket::trainId,
    ByteBufCodecs.COMPOUND_TAG, p -> p.variant.toNbt(),
    ByteBufCodecs.COMPOUND_TAG, p -> p.data.toNbt(),
    TrainAnnouncementEventPacket::deserializeNbt
  );

  private static TrainAnnouncementEventPacket deserializeNbt(UUID trainId, CompoundTag variant, CompoundTag data) {
    return new TrainAnnouncementEventPacket(trainId, AnnouncementVariant.fromNbt(variant), TrainDisplayData.fromNbt(data));
  }

  @Override
  public PacketTypeProvider getTypeProvider() {
    return TNetworking.C_TRAIN_ANNOUNCEMENT_EVENT;
  }

  @Override
  public void handle(LocalPlayer mc) {
    Train train = Create.RAILWAYS.trains.get(trainId);
    if (train == null) return;
    SoundEngine.enqueue(train, variant, data);
  }
}
