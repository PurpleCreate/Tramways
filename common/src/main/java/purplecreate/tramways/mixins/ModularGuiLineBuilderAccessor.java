package purplecreate.tramways.mixins;

import com.simibubi.create.foundation.gui.ModularGuiLine;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ModularGuiLineBuilder.class, remap = false)
public interface ModularGuiLineBuilderAccessor {
  @Accessor("target")
  ModularGuiLine tramways$getTarget();

  @Accessor("x")
  int tramways$getX();

  @Accessor("y")
  int tramways$getY();
}
