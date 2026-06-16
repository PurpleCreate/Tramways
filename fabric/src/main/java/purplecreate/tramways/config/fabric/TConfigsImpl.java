package purplecreate.tramways.config.fabric;

import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
import fuzs.forgeconfigapiport.api.config.v2.ModConfigEvents;
import net.createmod.catnip.config.ConfigBase;
import net.minecraftforge.fml.config.ModConfig;
import purplecreate.tramways.Tramways;
import purplecreate.tramways.config.TConfigs;

import java.util.Map;

public class TConfigsImpl {
  public static void register() {
    TConfigs.registerInternal();

    for (Map.Entry<ModConfig.Type, ConfigBase> pair : TConfigs.CONFIGS.entrySet())
      ForgeConfigRegistry.INSTANCE.register(Tramways.ID, pair.getKey(), pair.getValue().specification);

    ModConfigEvents.loading(Tramways.ID).register(TConfigs::onLoad);
    ModConfigEvents.reloading(Tramways.ID).register(TConfigs::onReload);
  }
}
