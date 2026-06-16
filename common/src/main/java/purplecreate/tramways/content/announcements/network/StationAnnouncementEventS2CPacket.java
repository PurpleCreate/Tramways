package purplecreate.tramways.content.announcements.network;

import de.mrjulsen.crn.data.train.portable.StationDisplayData;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import purplecreate.tramways.content.announcements.config.AnnouncementVariant;
import purplecreate.tramways.content.announcements.engine.SoundEngine;
import purplecreate.tramways.util.S2CPacket;

public class StationAnnouncementEventS2CPacket implements S2CPacket {
  private final BlockPos speakerPos;
  private final AnnouncementVariant variant;
  private final StationDisplayData data;

  public StationAnnouncementEventS2CPacket(BlockPos speakerPos, AnnouncementVariant variant, StationDisplayData data) {
    this.speakerPos = speakerPos;
    this.variant = variant;
    this.data = data;
  }

  public static StationAnnouncementEventS2CPacket read(FriendlyByteBuf buf) {
    return new StationAnnouncementEventS2CPacket(
      buf.readBlockPos(),
      AnnouncementVariant.fromNbt(buf.readNbt()),
      StationDisplayData.fromNbt(buf.readNbt())
    );
  }

  @Override
  public void write(FriendlyByteBuf buf) {
    buf.writeBlockPos(speakerPos);
    buf.writeNbt(variant.toNbt());
    buf.writeNbt(data.toNbt());
  }

  @Override
  public void handle(Minecraft mc) {
    SoundEngine.enqueue(speakerPos, variant, data);
  }
}
