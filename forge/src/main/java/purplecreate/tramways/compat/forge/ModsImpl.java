package purplecreate.tramways.compat.forge;

import net.minecraftforge.fml.ModList;

public class ModsImpl {
  public static boolean modLoaded(String id) {
    return ModList.get().isLoaded(id);
  }
}
