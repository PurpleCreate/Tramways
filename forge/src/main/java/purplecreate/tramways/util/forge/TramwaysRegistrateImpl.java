package purplecreate.tramways.util.forge;

import net.minecraftforge.eventbus.api.IEventBus;
import purplecreate.tramways.util.TramwaysRegistrate;

public class TramwaysRegistrateImpl extends TramwaysRegistrate {
  protected TramwaysRegistrateImpl(String modid) {
    super(modid);
  }

  public static TramwaysRegistrate create(String modid) {
    return new TramwaysRegistrateImpl(modid);
  }

  @Override
  public TramwaysRegistrate registerEventListeners(IEventBus bus) {
    return super.registerEventListeners(bus);
  }
}
