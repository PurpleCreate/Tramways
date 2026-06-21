package purplecreate.tramways.mixins;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProgressListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import purplecreate.tramways.events.CommonEvents;

@Mixin(value = ServerLevel.class)
public class ServerLevelMixin {
  @Inject(method = "save", at = @At("TAIL"))
  public void tramways$dispatchSaveEvent(ProgressListener progress, boolean flush, boolean skipSave, CallbackInfo ci) {
    if (!skipSave) {
      CommonEvents.onLevelSave((ServerLevel)(Object)this);
    }
  }
}
