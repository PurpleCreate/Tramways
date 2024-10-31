package purplecreate.tramways.content.signs.demands.forge;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.trains.HonkPacket;
import com.simibubi.create.content.trains.entity.Train;
import net.minecraftforge.network.PacketDistributor;

public class WhistleSignDemandImpl {
  public static void sendWhistlePacket(Train train, boolean isHonk) {
    AllPackets.getChannel().send(
      PacketDistributor.ALL.noArg(),
      new HonkPacket(train, isHonk)
    );
  }
}
