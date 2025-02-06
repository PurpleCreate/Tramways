package purplecreate.tramways.content.signs.schedule;

import com.simibubi.create.content.trains.schedule.destination.ChangeThrottleInstruction;
import net.minecraft.resources.ResourceLocation;
import purplecreate.tramways.Tramways;

public class SetPrimaryLimitInstruction extends ChangeThrottleInstruction {
  @Override
  public ResourceLocation getId() {
    return Tramways.rl("set_primary_limit");
  }
}
