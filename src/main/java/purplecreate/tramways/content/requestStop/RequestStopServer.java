package purplecreate.tramways.content.requestStop;

import com.simibubi.create.content.trains.entity.Train;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import purplecreate.tramways.content.requestStop.network.StoppingBroadcastPacket;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RequestStopServer {
  private static final Map<Train, Float> countdowns = new HashMap<>();
  private static final Set<Train> stopping = new HashSet<>();

  public static void request(Train train) {
    if (stopping.contains(train) || !countdowns.containsKey(train))
      return;
    stopping.add(train);
    countdowns.remove(train);
    notifyPassengers(train);
  }

  public static void removeRequest(Train train) {
    if (!stopping.contains(train))
      return;
    stopping.remove(train);
    notifyPassengers(train);
  }

  public static void updateCountdown(Train train, float countdown) {
    countdowns.put(train, countdown);
    notifyPassengers(train);
  }

  public static void removeCountdown(Train train) {
    if (!countdowns.containsKey(train))
      return;
    countdowns.remove(train);
    notifyPassengers(train);
  }

  public static boolean shouldStop(Train train) {
    return stopping.contains(train);
  }

  private static void notifyPassengers(Train train) {
    train.carriages.forEach(carriage ->
      carriage.forEachPresentEntity(cce ->
        cce.getIndirectPassengers().forEach(entity -> {
          if (entity instanceof Player player)
            notifyPassenger(train, player);
        })
      )
    );
  }

  private static void notifyPassenger(Train train, Player player) {
    if (player instanceof ServerPlayer serverPlayer) {
      StoppingBroadcastPacket.State type;
      float progress = 0f;

      if (stopping.contains(train)) {
        type = StoppingBroadcastPacket.State.SHOW_STOPPING;
      } else if (countdowns.containsKey(train)) {
        type = StoppingBroadcastPacket.State.UPDATE_COUNTDOWN;
        progress = countdowns.get(train);
      } else {
        type = StoppingBroadcastPacket.State.REMOVE;
      }

      CatnipServices.NETWORK.sendToClient(
        serverPlayer,
        new StoppingBroadcastPacket(
          type,
          progress,
          train.navigation.destination == null
            ? "?"
            : train.navigation.destination.name
        )
      );
    }
  }
}
