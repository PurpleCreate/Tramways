package purplecreate.tramways.config;

import net.createmod.catnip.config.ConfigBase;

public class ClientConfig extends ConfigBase {
  public final ConfigBool alwaysUseAdminMode = b(false, "alwaysUseAdminMode", "Always open file manager in admin mode");

  @Override
  public String getName() {
    return "client";
  }
}
