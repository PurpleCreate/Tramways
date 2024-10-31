package purplecreate.tramways.forge;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import purplecreate.tramways.TBlocks;
import purplecreate.tramways.Tramways;

public class TCreativeTabsImpl {
  private static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
    DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Tramways.ID);

  private static final RegistryObject<CreativeModeTab> BASE_CREATIVE_TAB =
    CREATIVE_TABS.register("main", () ->
      CreativeModeTab.builder()
        .title(Tramways.translatable("creative_tab.main"))
        .icon(TBlocks.TRAM_SIGN::asStack)
        .build()
    );

  public static ResourceKey<CreativeModeTab> getBaseTab() {
    return BASE_CREATIVE_TAB.getKey();
  }

  public static void register(IEventBus modEventBus) {
    CREATIVE_TABS.register(modEventBus);
  }
}
