package purplecreate.tramways.mixins;

import com.simibubi.create.content.trains.schedule.ScheduleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.Consumer;

@Mixin(value = ScheduleScreen.class, remap = false)
public interface ScheduleScreenAccessor {
  @Accessor("onEditorClose")
  Consumer<Boolean> tramways$getOnEditorClose();
}
