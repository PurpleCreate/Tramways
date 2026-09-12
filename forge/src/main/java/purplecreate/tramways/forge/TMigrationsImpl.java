package purplecreate.tramways.forge;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.MissingMappingsEvent;
import purplecreate.tramways.Tramways;

import java.util.Map;

import static purplecreate.tramways.TMigrations.*;

@Mod.EventBusSubscriber
public class TMigrationsImpl {
  private static <T> void remap(MissingMappingsEvent event, IForgeRegistry<T> registry, Map<String, ResourceLocation> map) {
    ResourceKey<Registry<T>> regKey = registry.getRegistryKey();
    for (MissingMappingsEvent.Mapping<T> mapping : event.getMappings(regKey, Tramways.ID)) {
      ResourceLocation newId = map.get(mapping.getKey().getPath());
      if (newId == null) continue;

      Tramways.LOGGER.info("Remapping {} '{}' to '{}'", regKey, mapping.getKey(), newId);
      try {
        T newObject = registry.getValue(newId);
        if (newObject == null) {
          throw new AssertionError("Registry object not present");
        }
        mapping.remap(newObject);
      } catch (Throwable e){
        Tramways.LOGGER.error("Could not remap {} '{}' to '{}'", regKey, mapping.getKey(), newId, e);
      }
    }
  }

  @SubscribeEvent
  public static void onMissingMappings(MissingMappingsEvent event) {
    remap(event, ForgeRegistries.BLOCKS, BLOCK_ID_CHANGES);
    remap(event, ForgeRegistries.ITEMS, ITEM_ID_CHANGES);
  }
}
