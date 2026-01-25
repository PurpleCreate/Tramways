package purplecreate.tramways.compat.neoforge;

import net.neoforged.fml.ModList;

public class ModsImpl {
  public static boolean modLoaded(String id) {
    return ModList.get().isLoaded(id);
  }
}
