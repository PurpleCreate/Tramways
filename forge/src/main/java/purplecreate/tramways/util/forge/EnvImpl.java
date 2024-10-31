package purplecreate.tramways.util.forge;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import purplecreate.tramways.util.Env;

public class EnvImpl {
  public static Env getEnv() {
    return FMLEnvironment.dist == Dist.CLIENT
      ? Env.CLIENT
      : Env.SERVER;
  }
}
