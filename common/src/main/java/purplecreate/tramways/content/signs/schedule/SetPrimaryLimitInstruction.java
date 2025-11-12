package purplecreate.tramways.content.signs.schedule;

import com.simibubi.create.content.trains.graph.DiscoveredPath;
import com.simibubi.create.content.trains.schedule.ScheduleRuntime;
import com.simibubi.create.content.trains.schedule.destination.ChangeThrottleInstruction;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import purplecreate.tramways.Tramways;
import purplecreate.tramways.mixinInterfaces.ITram;

public class SetPrimaryLimitInstruction extends ChangeThrottleInstruction {
  @Override
  public ResourceLocation getId() {
    return Tramways.rl("set_primary_limit");
  }

  @Override
  public void initConfigurationWidgets(ModularGuiLineBuilder builder) {
    builder.addScrollInput(0, 50, (si, l) -> {
      si
        .withRange(5, 101)
        .titled(CreateLang.translateDirect("schedule.instruction.throttle_edit_box"));
      l.withSuffix("%");
    }, "Value");
  }

  @Override
  public @Nullable DiscoveredPath start(ScheduleRuntime runtime, Level level) {
    if (runtime.train instanceof ITram tram) {
      tram.tramways$setPrimaryLimit(getThrottle());
    }

    return super.start(runtime, level);
  }
}
