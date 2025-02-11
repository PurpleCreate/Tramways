package purplecreate.tramways.content.signs.demands;

import com.jozufozu.flywheel.core.PartialModel;
import purplecreate.tramways.TPartialModels;

public abstract class AuxSignDemand extends SignDemand {
  public static final PartialModel TEXT_AUX_FACE = TPartialModels.TEXT_AUX_FACE;
  public static final PartialModel ADVANCE_WARNING_AUX_FACE = TPartialModels.ADVANCE_WARNING_AUX_FACE;

  @Override
  public boolean isAuxiliary() {
    return true;
  }
}
