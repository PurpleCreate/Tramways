package purplecreate.tramways;

import com.google.gson.JsonSyntaxException;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.network.chat.Component;
import purplecreate.tramways.config.Config;
import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import purplecreate.tramways.util.Env;
import purplecreate.tramways.util.TramwaysRegistrate;

import java.io.IOException;

import static net.createmod.catnip.lang.LangBuilder.resolveBuilders;

public class Tramways {
  public static final String ID = "tramways";
  public static final TramwaysRegistrate REGISTRATE =
    TramwaysRegistrate.create(ID)
      .defaultCreativeTab(TCreativeTabs.getBaseTab());
  public static final Logger LOGGER = LogUtils.getLogger();

  public static void init() {
    TBlocks.register();
    TItems.register();
    TBlockEntities.register();
    TSignalTypes.register();
    TTags.register();
    TExtras.register();

    Env.unsafeRunWhenOn(Env.CLIENT, () ->
      TPartialModels::register
    );

    try {
      Config.getInstance().write();
    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (JsonSyntaxException e) {
      throw new RuntimeException("Config for Tramways is malformed!", e);
    }
  }

  public static void commonSetup() {
    TNetworking.register();
    TExtras.registerCommon();
  }

  public static void clientSetup() {
    PonderIndex.addPlugin(new TPonders());
  }

  public static MutableComponent translatable(String path, Object... o) {
    return Component.translatable(Tramways.ID + "." + path, resolveBuilders(o));
  }

  public static ResourceLocation rl(String path) {
    return new ResourceLocation(ID, path);
  }
}
