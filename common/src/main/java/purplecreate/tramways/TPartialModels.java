package purplecreate.tramways;

import com.jozufozu.flywheel.core.PartialModel;

public class TPartialModels {
  public static final PartialModel TRAM_FACE = load("block/tram_sign/face");
  public static final PartialModel RED_RAILWAY_FACE = load("block/railway_sign/red_face");
  public static final PartialModel GREY_RAILWAY_FACE = load("block/railway_sign/grey_face");
  public static final PartialModel TSR_RAILWAY_FACE = load("block/railway_sign/tsr_face");

  public static final PartialModel SIGN_WOODEN_INNER = load("block/station_name_sign/wooden_inner");

  private static PartialModel load(String path) {
    return new PartialModel(Tramways.rl(path));
  }

  public static void register() {}
}
