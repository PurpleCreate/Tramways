package purplecreate.tramways.config;

import net.createmod.catnip.config.ConfigBase;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

@EventBusSubscriber
public class TConfigs {
  public static final Map<net.neoforged.fml.config.ModConfig.Type, ConfigBase> CONFIGS = new EnumMap<>(ModConfig.Type.class);

  private static ClientConfig client;
  private static ServerConfig server;

  public static ClientConfig client() {
    return client;
  }

  public static ServerConfig server() {
    return server;
  }

  private static <T extends ConfigBase> T register(Supplier<T> factory, ModConfig.Type side) {
    Pair<T, ModConfigSpec> specPair = (new ModConfigSpec.Builder()).configure((builder) -> {
      T config = factory.get();
      config.registerAll(builder);
      return config;
    });

    T config = specPair.getLeft();
    config.specification = specPair.getRight();
    CONFIGS.put(side, config);
    return config;
  }

  @SubscribeEvent
  public static void onLoad(ModConfigEvent.Loading event) {
    for (ConfigBase config : CONFIGS.values()) {
      if (config.specification == event.getConfig().getSpec()) {
        config.onLoad();
      }
    }
  }

  @SubscribeEvent
  public static void onReload(ModConfigEvent.Reloading event) {
    for (ConfigBase config : CONFIGS.values()) {
      if (config.specification == event.getConfig().getSpec()) {
        config.onReload();
      }
    }
  }

  public static void register(ModContainer context) {
    client = register(ClientConfig::new, ModConfig.Type.CLIENT);
    server = register(ServerConfig::new, ModConfig.Type.SERVER);

    for (Map.Entry<ModConfig.Type, ConfigBase> pair : TConfigs.CONFIGS.entrySet())
      context.registerConfig(pair.getKey(), pair.getValue().specification);
  }
}
