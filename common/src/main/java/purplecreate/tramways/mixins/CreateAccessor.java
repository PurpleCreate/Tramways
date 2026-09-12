package purplecreate.tramways.mixins;

import com.simibubi.create.Create;
import com.simibubi.create.foundation.data.CreateRegistrate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = Create.class, remap = false)
public interface CreateAccessor {
  @Accessor(value = "REGISTRATE")
  static CreateRegistrate getRegistrate() {
    throw new AssertionError();
  }
}