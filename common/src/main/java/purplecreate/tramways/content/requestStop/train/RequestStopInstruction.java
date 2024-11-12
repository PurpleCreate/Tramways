package purplecreate.tramways.content.requestStop.train;

import com.simibubi.create.content.trains.schedule.destination.DestinationInstruction;
import net.minecraft.resources.ResourceLocation;
import purplecreate.tramways.Tramways;

public class RequestStopInstruction extends DestinationInstruction {
  @Override
  public ResourceLocation getId() {
    return Tramways.rl("request_stop");
  }
}
