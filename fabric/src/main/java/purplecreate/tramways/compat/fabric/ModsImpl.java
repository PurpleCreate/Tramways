package purplecreate.tramways.compat.fabric;

import net.fabricmc.loader.api.FabricLoader;

public class ModsImpl {
  public static boolean modLoaded(String id) {
    return FabricLoader.getInstance().isModLoaded(id);
  }
}
