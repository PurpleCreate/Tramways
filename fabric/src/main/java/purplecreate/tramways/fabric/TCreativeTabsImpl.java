package purplecreate.tramways.fabric;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import purplecreate.tramways.TBlocks;
import purplecreate.tramways.Tramways;

public class TCreativeTabsImpl {
  public static ResourceKey<CreativeModeTab> getBaseTab() {
    return ResourceKey.create(
      Registries.CREATIVE_MODE_TAB,
      Tramways.rl("main")
    );
  }

  public static void register() {
    Registry.register(
      BuiltInRegistries.CREATIVE_MODE_TAB,
      getBaseTab(),
      FabricItemGroup.builder()
        .title(Tramways.translatable("creative_tab.main"))
        .icon(TBlocks.TRAM_SIGN::asStack)
        .build()
    );
  }
}
