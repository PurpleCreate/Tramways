package purplecreate.tramways.mixins.fabric;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import purplecreate.tramways.Tramways;
import purplecreate.tramways.content.signals.render.SignalTypes;

@Mixin(SignalTypes.class)
public abstract class SignalTypesFabricMixin implements IdentifiableResourceReloadListener {
  @Override
  public ResourceLocation getFabricId() {
    return Tramways.rl("signal_types");
  }
}
