package purplecreate.tramways.content.signals.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Map;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class Aspects {
  private static final Map<String, Supplier<? extends Aspect>> ASPECT_TYPES = Map.of(
    "square", SquareAspect::new,
    "string", StringAspect::new
  );

  public static Aspect create(String type) {
    return ASPECT_TYPES.get(type).get();
  }
}
