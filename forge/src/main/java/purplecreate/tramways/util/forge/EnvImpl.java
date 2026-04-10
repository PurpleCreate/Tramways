package purplecreate.tramways.util.forge;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLLoader;
import purplecreate.tramways.Tramways;
import purplecreate.tramways.util.Env;

public class EnvImpl {
  public static Env getEnv() {
    return FMLEnvironment.dist == Dist.CLIENT
      ? Env.CLIENT
      : Env.SERVER;
  }

  public static boolean isDevVersion() {
    return !FMLLoader.isProduction() || getVersion().contains("-pre-");
  }

  public static String getVersion() {
    return FMLLoader.getLoadingModList().getModFileById(Tramways.ID).versionString();
  }
}
