package purplecreate.tramways;

import com.simibubi.create.foundation.data.CreateRegistrate;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.network.chat.Component;
import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;
import purplecreate.tramways.config.TConfigs;
import purplecreate.tramways.content.announcements.engine.tts.TTSVoices;
import purplecreate.tramways.util.Env;

import static net.createmod.catnip.lang.LangBuilder.resolveBuilders;

@Mod(Tramways.ID)
public class Tramways {
  public static final String ID = "tramways";
  public static final CreateRegistrate REGISTRATE =
    CreateRegistrate.create(ID)
      .defaultCreativeTab(TCreativeTabs.getBaseTab());
  public static final Logger LOGGER = LogUtils.getLogger();

  public Tramways(IEventBus modEventBus, ModContainer container) {
    Tramways.REGISTRATE.registerEventListeners(modEventBus);

    TBlocks.register();
    TBlockEntities.register();
    TTags.register();
    TConfigs.register(container);
    TCreativeTabs.register(modEventBus);
    TExtras.register();

    modEventBus.addListener(this::commonSetup);
    modEventBus.addListener(this::clientSetup);

    Env.unsafeRunWhenOn(Env.CLIENT, () ->
      TPartialModels::register
    );
  }

  public void commonSetup(FMLCommonSetupEvent event) {
    event.enqueueWork(() -> {
      TNetworking.register();
    });
  }

  public void clientSetup(FMLClientSetupEvent event) {
    event.enqueueWork(() -> {
      PonderIndex.addPlugin(new TPonders());
      TTSVoices.init();
    });
  }

  public static MutableComponent translatable(String path, Object... o) {
    return Component.translatable(Tramways.ID + "." + path, resolveBuilders(o));
  }

  public static ResourceLocation rl(String path) {
    return ResourceLocation.fromNamespaceAndPath(ID, path);
  }
}
