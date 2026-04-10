package purplecreate.tramways.content.signals.item;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import purplecreate.tramways.Tramways;

import java.util.*;

public class CycleRegistries {
  private static final Map<ResourceLocation, ItemInfo> info = new HashMap<>();
  public static final CycleRegistry SIGNALS = new CycleRegistry();

  private static void putItemInfo(Item item, CycleRegistry registry, String category) {
    info.put(BuiltInRegistries.ITEM.getKey(item), new ItemInfo(registry, category));
  }

  public static ItemInfo getItemInfo(Item item) {
    return info.get(BuiltInRegistries.ITEM.getKey(item));
  }

  public static boolean compareRegistry(Item a, Item b) {
    ItemInfo ia = getItemInfo(a);
    ItemInfo ib = getItemInfo(b);
    return ia != null && ib != null && ia.registry.id == ib.registry.id;
  }

  public static class CycleRegistry {
    private static int idCounter = 0;
    private final int id = idCounter++;
    private final Map<String, Set<CyclableItem>> items = new HashMap<>();

    public void register(String category, CyclableItem item) {
      items.computeIfAbsent(category, k -> new HashSet<>()).add(item);
      putItemInfo(item, this, category);
    }

    public Map<String, Set<CyclableItem>> get() {
      return items;
    }
  }

  public record ItemInfo(CycleRegistry registry, String category) {
  }
}
