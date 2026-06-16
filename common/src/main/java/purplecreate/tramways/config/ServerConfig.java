package purplecreate.tramways.config;

import net.createmod.catnip.config.ConfigBase;

public class ServerConfig extends ConfigBase {
  public final ConfigInt fileSizeLimitKbytes = i(4000, 1, "fileSizeLimitKbytes", "Maximum file size for uploads in kilobytes");
  public final ConfigInt fileCountLimit = i(2000, 1, "fileCountLimit", "Maximum amount of files allowed per player");
  public final ConfigInt fileChunkSizeBytes = i(500000, 1, "fileChunkSizeBytes", "Don't touch if you don't know what you're doing! Maximum chunk size sent per tick, can speed up uploads but will cause crashes if raised too high");
  public final ConfigBool adminsBypassLimit = b(false, "adminsBypassLimit", "Admins can bypass upload limits in admin mode");

  @Override
  public String getName() {
    return "server";
  }
}
