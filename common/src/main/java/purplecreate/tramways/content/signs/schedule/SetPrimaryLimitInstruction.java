package purplecreate.tramways.content.signs.schedule;

import com.simibubi.create.content.trains.schedule.destination.ChangeThrottleInstruction;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.resources.ResourceLocation;
import purplecreate.tramways.Tramways;

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
        .titled(Lang.translateDirect("schedule.instruction.throttle_edit_box"));
      l.withSuffix("%");
    }, "Value");
  }
}
