package purplecreate.tramways.mixins;

import purplecreate.tramways.content.signs.TramSignPoint;
import com.simibubi.create.content.trains.entity.Navigation;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.graph.TrackNode;
import com.simibubi.create.content.trains.signal.TrackEdgePoint;
import com.simibubi.create.foundation.utility.Couple;
import com.simibubi.create.foundation.utility.Pair;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(value = Navigation.class, remap = false)
public class NavigationMixin {
  @Shadow public Train train;

  @Inject(method = "lambda$tick$0", at = @At("HEAD"))
  private void tramways$lambda$tick$0(MutableObject<Pair<UUID, Boolean>> trackingCrossSignal,
                                     double scanDistance,
                                     MutableDouble crossSignalDistanceTracker,
                                     double brakingDistanceNoFlicker,
                                     Double distance,
                                     Pair<TrackEdgePoint, Couple<TrackNode>> couple,
                                     CallbackInfoReturnable<Boolean> cir) {
    if (couple.getFirst() instanceof TramSignPoint sign) {
      TrackNode node = couple.getSecond().getSecond();
      sign.updateTrain(train, node, distance);
    }
  }
}
