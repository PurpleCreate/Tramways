package purplecreate.tramways.mixins;

import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.graph.DiscoveredPath;
import com.simibubi.create.content.trains.schedule.ScheduleRuntime;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import purplecreate.tramways.content.requestStop.RequestStopServer;
import purplecreate.tramways.mixinInterfaces.IStopRequestableNavigation;

@Mixin(value = ScheduleRuntime.class, remap = false)
public class ScheduleRuntimeMixin {
  @Unique private int tramways$lastEntry = -1;
  @Shadow private Train train;
  @Shadow public int currentEntry;

  @Inject(method = "startCurrentInstruction", at = @At("HEAD"))
  private void tramways$resetRequestStop(CallbackInfoReturnable<DiscoveredPath> cir) {
    if (tramways$lastEntry == currentEntry)
      return;

    tramways$lastEntry = currentEntry;

    RequestStopServer.removeRequest(train);
    if (train.navigation instanceof IStopRequestableNavigation nav)
      nav.tramways$resetRouteCancelled();
  }
}
