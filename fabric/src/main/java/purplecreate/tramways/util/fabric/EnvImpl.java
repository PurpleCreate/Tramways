package purplecreate.tramways.util.fabric;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import purplecreate.tramways.Tramways;
import purplecreate.tramways.util.Env;

public class EnvImpl {
  public static Env getEnv() {
    return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT
      ? Env.CLIENT
      : Env.SERVER;
  }

  public static boolean isDevVersion() {
    return !FabricLoader.getInstance().isDevelopmentEnvironment() || getVersion().contains("-pre-");
  }

  public static String getVersion() {
    return FabricLoader
      .getInstance()
      .getModContainer(Tramways.ID)
      .map(container -> container.getMetadata().getVersion().getFriendlyString())
      .orElse("unknown");
  }
}
