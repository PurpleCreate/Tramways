package purplecreate.tramways;

import com.simibubi.create.foundation.data.CreateRegistrate;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.network.chat.Component;
import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import purplecreate.tramways.content.announcements.engine.tts.TTSVoices;
import purplecreate.tramways.util.Env;

import static net.createmod.catnip.lang.LangBuilder.resolveBuilders;

public class Tramways {
  public static final String ID = "tramways";
  public static final CreateRegistrate REGISTRATE =
    CreateRegistrate.create(ID)
      .defaultCreativeTab(TCreativeTabs.getBaseTab());
  public static final Logger LOGGER = LogUtils.getLogger();

  public static void init() {
    TBlocks.register();
    TBlockEntities.register();
    TTags.register();

    Env.unsafeRunWhenOn(Env.CLIENT, () ->
      TPartialModels::register
    );
  }

  public static void commonSetup() {
    TNetworking.register();
    TExtras.registerCommon();
  }

  public static void clientSetup() {
    PonderIndex.addPlugin(new TPonders());
    TTSVoices.init();
  }

  public static MutableComponent translatable(String path, Object... o) {
    return Component.translatable(Tramways.ID + "." + path, resolveBuilders(o));
  }

  public static ResourceLocation rl(String path) {
    return new ResourceLocation(ID, path);
  }
}
