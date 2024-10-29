package purplecreate.tramways;

import com.tterrag.registrate.Registrate;
import purplecreate.tramways.config.Config;
import purplecreate.tramways.datagen.DataGen;
import purplecreate.tramways.services.FFMPEG;
import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.utility.Components;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.io.IOException;

import static com.simibubi.create.foundation.utility.Lang.resolveBuilders;

@Mod(Tramways.ID)
public class Tramways {
  public static final String ID = "tramways";
  public static final Registrate REGISTRATE =
    Registrate.create(ID)
      .defaultCreativeTab(TCreativeTabs.BASE_CREATIVE_TAB.getKey());
  public static final Logger LOGGER = LogUtils.getLogger();

  public Tramways() {
    IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

    TCreativeTabs.register(modEventBus);
    TBlocks.register();
    TBlockEntities.register();
    TTags.register();
    TPonders.register();
    DataGen.register();

    modEventBus.addListener(this::commonSetup);
    modEventBus.addListener(this::clientSetup);

    try {
      Config.read().write();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void commonSetup(final FMLCommonSetupEvent event) {
    event.enqueueWork(() -> {
      TNetworking.register();
      TExtras.registerCommon();
    });
  }

  private void clientSetup(final FMLClientSetupEvent event) {
    event.enqueueWork(() -> {
      if (!FFMPEG.exists()) {
        throw new RuntimeException(
          "FFMPEG is missing! Create: Tramways requires this to run properly. "
            + "Please visit https://ffmpeg.org/download.html to download ffmpeg "
            + "and don't forget to add the install location to your PATH."
        );
      }
    });
  }

  public static MutableComponent translatable(String path, Object... o) {
    return Components.translatable(Tramways.ID + "." + path, resolveBuilders(o));
  }

  public static ResourceLocation rl(String path) {
    return new ResourceLocation(ID, path);
  }
}
