package purplecreate.tramways.mixins;

import net.minecraft.client.resources.SplashManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import purplecreate.tramways.util.Env;

import java.util.List;

@Mixin(SplashManager.class)
public class SplashManagerMixin {
  @Shadow @Final private List<String> splashes;

  @Inject(method = "apply(Ljava/util/List;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At("TAIL"))
  private void tramways$addSplash(List<String> object, ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo ci) {
    if (Env.isDevVersion()) {
      splashes.clear();
      splashes.add("Create: Tramways");
      splashes.add("Hello from C1200");
    }
    splashes.add("bub");
    splashes.add("Tram Additions != Tramways");
  }
}
