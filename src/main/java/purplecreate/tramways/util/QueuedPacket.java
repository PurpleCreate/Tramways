package purplecreate.tramways.util;

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
        long estimatedDuration = queue.get(key).remove(0).handleQueued();
        timeout.put(key, time + estimatedDuration);
      }
    }
  }

  protected void addToQueue(String key) {
    if (!queue.containsKey(key)) {
      queue.put(key, new ArrayList<>());
    }

    queue.get(key).add(this);
  }

  abstract protected long handleQueued();
}
