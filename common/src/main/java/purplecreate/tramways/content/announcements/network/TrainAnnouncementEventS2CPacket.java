package purplecreate.tramways.content.announcements.network;

import com.simibubi.create.Create;
import com.simibubi.create.content.trains.entity.Train;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import purplecreate.tramways.content.announcements.config.AnnouncementVariant;
import purplecreate.tramways.content.announcements.engine.SoundEngine;
import purplecreate.tramways.content.announcements.train.TrainEntry;
import purplecreate.tramways.util.S2CPacket;

import java.util.UUID;

public class TrainAnnouncementEventS2CPacket implements S2CPacket {
  private final UUID trainId;
  private final AnnouncementVariant variant;
  private final TrainEntry data;

  public TrainAnnouncementEventS2CPacket(UUID trainId, AnnouncementVariant variant, TrainEntry data) {
    this.trainId = trainId;
    this.variant = variant;
    this.data = data;
  }

  public static TrainAnnouncementEventS2CPacket read(FriendlyByteBuf buf) {
    return new TrainAnnouncementEventS2CPacket(
      buf.readUUID(),
      AnnouncementVariant.fromNbt(buf.readNbt()),
      TrainEntry.fromNbt(buf.readNbt())
    );
  }

  @Override
  public void write(FriendlyByteBuf buf) {
    buf.writeUUID(trainId);
    buf.writeNbt(variant.toNbt());
    buf.writeNbt(data.toNbt());
  }

  @Override
  public void handle(Minecraft mc) {
    Train train = Create.RAILWAYS.trains.get(trainId);
    if (train == null) return;
    SoundEngine.enqueue(train, variant, data);
  }
}
