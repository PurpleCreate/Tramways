package purplecreate.tramways.content.signs.demands.fabric;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.trains.HonkPacket;
import com.simibubi.create.content.trains.entity.Train;
import io.github.fabricators_of_create.porting_lib.util.ServerLifecycleHooks;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;

public class WhistleSignDemandImpl {
  public static void sendWhistlePacket(Train train, boolean isHonk) {
    AllPackets.getChannel().sendToClients(
      new HonkPacket(train, isHonk),
      PlayerLookup.all(ServerLifecycleHooks.getCurrentServer())
    );
  }
}
