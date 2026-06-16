package purplecreate.tramways.content.announcements.station.forge;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkConfigurationPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

public class StationSpeakerDisplaySourceImpl {
  public static void sendConfigurationPacket(BlockPos pos, CompoundTag configData, int targetLine) {
    AllPackets.getChannel().sendToServer(new DisplayLinkConfigurationPacket(pos, configData, targetLine));
  }
}
