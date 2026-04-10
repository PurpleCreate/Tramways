package purplecreate.tramways.content.signals.models;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.resources.model.BakedModel;

public class SignAttachedToPoleModel {
  @ExpectPlatform
  public static BakedModel create(BakedModel wrapped) {
    throw new AssertionError();
  }
}
