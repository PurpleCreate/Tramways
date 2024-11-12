package purplecreate.tramways.fabric;

import com.tterrag.registrate.fabric.EnvExecutor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import purplecreate.tramways.Tramways;
import purplecreate.tramways.events.fabric.ClientEventsImpl;
import purplecreate.tramways.events.fabric.CommonEventsImpl;

public class TramwaysImpl implements ModInitializer {
  @Override
  public void onInitialize() {
    Tramways.init();
    Tramways.REGISTRATE.register();
    Tramways.commonSetup();
    TCreativeTabsImpl.register();
    TNetworkingImpl.init();
    CommonEventsImpl.register();

    EnvExecutor.runWhenOn(EnvType.CLIENT, () -> () -> {
      Tramways.clientSetup();
      ClientEventsImpl.register();
    });
  }
}
