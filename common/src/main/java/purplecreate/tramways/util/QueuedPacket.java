package purplecreate.tramways.util;

import purplecreate.tramways.Tramways;

import java.util.*;

public abstract class QueuedPacket {
  private static final Map<String, Long> timeout = new HashMap<>();
  private static final Map<String, List<QueuedPacket>> queue = new HashMap<>();

  public static void tick() {
    long time = new Date().getTime();

    for (String key : queue.keySet()) {
      if (
        time > timeout.getOrDefault(key, 0L)
          && queue.containsKey(key)
          && !queue.get(key).isEmpty()
      ) {
        QueuedPacket packet = queue.get(key).remove(0);
        long estimatedDuration = packet.handleQueued();
        timeout.put(key, time + estimatedDuration);
      }
    }
  }

  public void addToQueue(String key) {
    String uukey = this.getClass() + key;

    if (!queue.containsKey(uukey)) {
      queue.put(uukey, new ArrayList<>());
    }

    queue.get(uukey).add(this);
  }

  abstract public long handleQueued();
}
