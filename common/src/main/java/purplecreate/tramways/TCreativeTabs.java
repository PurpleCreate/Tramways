package purplecreate.tramways;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;

public class TCreativeTabs {
  @ExpectPlatform
  public static ResourceKey<CreativeModeTab> getBaseTab() {
    throw new AssertionError();
  }
}
