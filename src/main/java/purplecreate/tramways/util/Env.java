package purplecreate.tramways.util;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLLoader;
import purplecreate.tramways.Tramways;

import java.util.function.Supplier;

public enum Env {
  CLIENT,
  SERVER;

  public static Env getEnv() {
    return FMLEnvironment.dist == Dist.CLIENT
      ? Env.CLIENT
      : Env.SERVER;
  }

  public static String getVersion() {
    return FMLLoader.getLoadingModList().getModFileById(Tramways.ID).versionString();
  }

  public static void unsafeRunWhenOn(Env env, Supplier<Runnable> toRun) {
    if (getEnv() == env) {
      toRun.get().run();
    }
  }
}
