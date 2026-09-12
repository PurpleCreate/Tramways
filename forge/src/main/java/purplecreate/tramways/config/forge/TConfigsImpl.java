package purplecreate.tramways.config.forge;

import net.createmod.catnip.config.ConfigBase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import purplecreate.tramways.config.TConfigs;

import java.util.Map;

public class TConfigsImpl {
  public static void register(ModLoadingContext context) {
    TConfigs.registerInternal();

    for (Map.Entry<ModConfig.Type, ConfigBase> pair : TConfigs.CONFIGS.entrySet())
      context.registerConfig(pair.getKey(), pair.getValue().specification);
  }

  @SubscribeEvent
  public static void onLoad(ModConfigEvent.Loading event) {
    TConfigs.onLoad(event.getConfig());
  }

  @SubscribeEvent
  public static void onReload(ModConfigEvent.Reloading event) {
    TConfigs.onReload(event.getConfig());
  }
}
