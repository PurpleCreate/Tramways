package purplecreate.tramways.fabric;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;

public class TKeybindsImpl {
  public static void register(KeyMapping key) {
    KeyBindingHelper.registerKeyBinding(key);
  }

  public static int get(KeyMapping key) {
    return KeyBindingHelper.getBoundKeyOf(key).getValue();
  }
}
