package purplecreate.tramways;

import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class TMigrations {
  public static Map<String, ResourceLocation> BLOCK_ID_CHANGES = Map.of(
    "tram_signal", Tramways.rl("generic_signal"),
    "four_aspect_signal", Tramways.rl("generic_signal"),
    "theatre_signal", Tramways.rl("generic_signal")
  );

  public static Map<String, ResourceLocation> ITEM_ID_CHANGES = Map.of(
    "tram_signal", Tramways.rl("generic_signal"),
    "four_aspect_signal", Tramways.rl("generic_signal"),
    "theatre_signal", Tramways.rl("generic_signal")
  );
}
