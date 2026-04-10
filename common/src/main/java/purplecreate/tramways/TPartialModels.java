package purplecreate.tramways;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public class TPartialModels {
  public static final PartialModel TRAM_FACE = load("block/tram_sign/face");
  public static final PartialModel RED_RAILWAY_FACE = load("block/railway_sign/red_face");
  public static final PartialModel GREY_RAILWAY_FACE = load("block/railway_sign/grey_face");
  public static final PartialModel TSR_RAILWAY_FACE = load("block/railway_sign/tsr_face");

  public static final PartialModel TEXT_AUX_FACE = load("block/auxiliary_sign/text_face");
  public static final PartialModel ADVANCE_WARNING_AUX_FACE = load("block/auxiliary_sign/advance_warning_face");

  public static final PartialModel SIGN_WOODEN_INNER_A = load("block/station_name_sign/wooden_inner_a");
  public static final PartialModel SIGN_WOODEN_INNER_B = load("block/station_name_sign/wooden_inner_b");

  public static final PartialModel ASPECT = load("block/signals/aspect");
  public static final PartialModel GIRDER = load("create", "block/metal_girder/block_pole");

  private static PartialModel load(String namespace, String path) {
    return PartialModel.of(new ResourceLocation(namespace, path));
  }

  private static PartialModel load(String path) {
    return load(Tramways.ID, path);
  }

  public static void register() {}
}
