package purplecreate.tramways.mixins;

import com.simibubi.create.content.trains.graph.DiscoveredPath;
import com.simibubi.create.content.trains.schedule.ScheduleRuntime;
import de.mrjulsen.crn.data.schedule.instruction.TravelSectionInstruction;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import purplecreate.tramways.content.announcements.ExtendedTrainData;

@Mixin(value = TravelSectionInstruction.class, remap = false)
public class TravelSectionInstructionMixin {
  @Inject(method = "start", at = @At("HEAD"))
  public void tramways$beginTrip(ScheduleRuntime runtime, Level level, CallbackInfoReturnable<DiscoveredPath> cir) {
    ExtendedTrainData.beginTrip(runtime.train);
  }
}
