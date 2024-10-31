package purplecreate.tramways.util.fabric;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import purplecreate.tramways.util.Env;

public class EnvImpl {
  public static Env getEnv() {
    return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT
      ? Env.CLIENT
      : Env.SERVER;
  }
}
