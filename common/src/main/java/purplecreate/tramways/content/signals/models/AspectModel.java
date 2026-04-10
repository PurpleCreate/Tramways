package purplecreate.tramways.content.signals.models;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.resources.model.BakedModel;

public class AspectModel {
  @ExpectPlatform
  public static BakedModel create(float radius) {
    throw new AssertionError();
  }
}
