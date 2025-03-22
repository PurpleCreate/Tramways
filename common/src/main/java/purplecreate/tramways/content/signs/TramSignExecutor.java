package purplecreate.tramways.content.signs;

import com.simibubi.create.Create;
import com.simibubi.create.content.trains.graph.TrackGraph;
import net.minecraft.world.level.Level;
import purplecreate.tramways.TExtras.EdgePointTypes;

public class TramSignExecutor {
  public static void tick(Level level) {
    for (TrackGraph graph : Create.RAILWAYS.trackNetworks.values())
      for (TramSignPoint point : graph.getPoints(EdgePointTypes.TRAM_SIGN))
        point.iterateQueue(level);
  }
}
