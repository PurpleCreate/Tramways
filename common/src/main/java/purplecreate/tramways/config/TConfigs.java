package purplecreate.tramways.config;

import net.createmod.catnip.config.ConfigBase;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

public class TConfigs {
  public static final Map<ModConfig.Type, ConfigBase> CONFIGS = new EnumMap<>(ModConfig.Type.class);

  private static ClientConfig client;
  private static ServerConfig server;

  public static ClientConfig client() {
    return client;
  }

  public static ServerConfig server() {
    return server;
  }

  private static <T extends ConfigBase> T register(Supplier<T> factory, ModConfig.Type side) {
    Pair<T, ForgeConfigSpec> specPair = (new ForgeConfigSpec.Builder()).configure((builder) -> {
      T config = factory.get();
      config.registerAll(builder);
      return config;
    });

    T config = specPair.getLeft();
    config.specification = specPair.getRight();
    CONFIGS.put(side, config);
    return config;
  }

  public static void registerInternal() {
    client = register(ClientConfig::new, ModConfig.Type.CLIENT);
    server = register(ServerConfig::new, ModConfig.Type.SERVER);
  }

  public static void onLoad(ModConfig modConfig) {
    for (ConfigBase config : CONFIGS.values()) {
      if (config.specification == modConfig.getSpec()) {
        config.onLoad();
      }
    }
  }

  public static void onReload(ModConfig modConfig) {
    for (ConfigBase config : CONFIGS.values()) {
      if (config.specification == modConfig.getSpec()) {
        config.onReload();
      }
    }
  }
}
