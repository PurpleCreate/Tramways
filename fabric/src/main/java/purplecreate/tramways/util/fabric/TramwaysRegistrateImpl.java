package purplecreate.tramways.util.fabric;

import com.simibubi.create.foundation.item.render.CustomRenderedItems;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import purplecreate.tramways.util.IHaveItemRenderer;
import purplecreate.tramways.util.TramwaysRegistrate;

public class TramwaysRegistrateImpl extends TramwaysRegistrate {
  protected TramwaysRegistrateImpl(String modid) {
    super(modid);

    addRegisterCallback(Registries.ITEM, () -> {
      getAll(Registries.ITEM).forEach(entry -> {
        Item item = entry.get();
        if (item instanceof IHaveItemRenderer r) {
          BuiltinItemRendererRegistry.INSTANCE.register(item, r.getRenderer());
          CustomRenderedItems.register(item);
        }
      });
    });
  }

  public static TramwaysRegistrate create(String modid) {
    return new TramwaysRegistrateImpl(modid);
  }
}
