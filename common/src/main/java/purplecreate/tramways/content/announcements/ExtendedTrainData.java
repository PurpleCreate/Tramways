package purplecreate.tramways.content.announcements;

import com.simibubi.create.content.trains.entity.Train;

import java.util.*;

public class ExtendedTrainData {
  private static final Map<UUID, UUID> TRIP_IDS = new HashMap<>();

  public static void beginTrip(Train train) {
    TRIP_IDS.put(train.id, UUID.randomUUID());
  }

  public static UUID getTripId(Train train) {
    return TRIP_IDS.computeIfAbsent(train.id, k -> UUID.randomUUID());
  }

  public static Set<UUID> getTripIds() {
    return new HashSet<>(TRIP_IDS.values());
  }
}
