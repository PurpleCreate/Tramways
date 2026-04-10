package purplecreate.tramways.mixins.forge;

import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import purplecreate.tramways.util.IHaveItemRenderer;

@Mixin(Item.class)
public class ItemMixin {
  @Shadow private Object renderProperties;

  @Inject(method = "initClient", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;initializeClient(Ljava/util/function/Consumer;)V"), remap = false, cancellable = true)
  private void tramways$registerItemRenderer(CallbackInfo ci) {
    if (this instanceof IHaveItemRenderer r) {
      renderProperties = SimpleCustomRenderer.create((Item)(Object)this, r.getRenderer());
      ci.cancel();
    }
  }
}
