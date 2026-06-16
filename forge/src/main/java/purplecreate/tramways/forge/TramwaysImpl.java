package purplecreate.tramways.forge;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import purplecreate.tramways.Tramways;
import purplecreate.tramways.config.forge.TConfigsImpl;
import purplecreate.tramways.util.forge.TramwaysRegistrateImpl;

@Mod(Tramways.ID)
public class TramwaysImpl {
  public TramwaysImpl() {
    IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

    Tramways.init();
    TConfigsImpl.register(ModLoadingContext.get());
    ((TramwaysRegistrateImpl) Tramways.REGISTRATE).registerEventListeners(modEventBus);
    TCreativeTabsImpl.register(modEventBus);
    modEventBus.addListener(this::commonSetup);
    modEventBus.addListener(this::clientSetup);
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
