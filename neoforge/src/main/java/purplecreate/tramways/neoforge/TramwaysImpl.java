package purplecreate.tramways.neoforge;

import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import purplecreate.tramways.Tramways;

@Mod(Tramways.ID)
public class TramwaysImpl {
  public TramwaysImpl(IEventBus modEventBus, ModContainer container) {
    Tramways.REGISTRATE.registerEventListeners(modEventBus);
    Tramways.init();
    TCreativeTabsImpl.register(modEventBus);
    modEventBus.addListener(this::commonSetup);
    modEventBus.addListener(this::clientSetup);
    modEventBus.addListener(EventPriority.HIGHEST, NeoDataGen::gatherDataFirst);
    modEventBus.addListener(EventPriority.LOWEST, NeoDataGen::gatherData);
  }

  private void commonSetup(final FMLCommonSetupEvent event) {
    event.enqueueWork(() -> {
      TNetworkingImpl.init();
      Tramways.commonSetup();
    });
  }

  private void clientSetup(final FMLClientSetupEvent event) {
    event.enqueueWork(Tramways::clientSetup);
  }
}
