package purplecreate.tramways.forge;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class TKeybindsImpl {
  private static final List<KeyMapping> keys = new ArrayList<>();

  public static void register(KeyMapping key) {
    keys.add(key);
  }

  public static int get(KeyMapping key) {
    return key.getKey().getValue();
  }

  @SubscribeEvent
  public static void onRegister(RegisterKeyMappingsEvent event) {
    keys.forEach(event::register);
  }
}
