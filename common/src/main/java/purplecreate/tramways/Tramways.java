package purplecreate.tramways;

import com.tterrag.registrate.Registrate;
import purplecreate.tramways.config.Config;
import purplecreate.tramways.datagen.DataGen;
import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.utility.Components;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

import java.io.IOException;

import static com.simibubi.create.foundation.utility.Lang.resolveBuilders;

public class Tramways {
  public static final String ID = "tramways";
  public static final Registrate REGISTRATE =
    Registrate.create(ID)
      .defaultCreativeTab(TCreativeTabs.getBaseTab());
  public static final Logger LOGGER = LogUtils.getLogger();

  public static void init() {
    TBlocks.register();
    TBlockEntities.register();
    TTags.register();
    TPonders.register();
    DataGen.register();

    try {
      Config.read().write();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void commonSetup() {
    TNetworking.register();
    TExtras.registerCommon();
  }

  public static void clientSetup() {}

  public static MutableComponent translatable(String path, Object... o) {
    return Components.translatable(Tramways.ID + "." + path, resolveBuilders(o));
  }

  public static ResourceLocation rl(String path) {
    return new ResourceLocation(ID, path);
  }
}
