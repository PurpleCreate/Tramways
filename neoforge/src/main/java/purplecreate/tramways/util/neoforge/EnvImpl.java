package purplecreate.tramways.util.neoforge;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import purplecreate.tramways.util.Env;

public class EnvImpl {
  public static Env getEnv() {
    return FMLEnvironment.dist == Dist.CLIENT
      ? Env.CLIENT
      : Env.SERVER;
  }
}
