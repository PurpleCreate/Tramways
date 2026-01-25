package purplecreate.tramways.neoforge;

import net.minecraft.data.DataGenerator;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import purplecreate.tramways.datagen.DataGen;

public class NeoDataGen {
  public static void gatherDataFirst(GatherDataEvent event) {
    DataGen.register();
  }

  public static void gatherData(GatherDataEvent event) {
    DataGenerator generator = event.getGenerator();
    DataGen.registerNonRegistrate((factory) -> generator.addProvider(event.includeServer(), factory));
  }
}
