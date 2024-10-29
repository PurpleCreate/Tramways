package purplecreate.tramways.mixins;

import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import purplecreate.tramways.content.signs.TramSignPoint;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.entity.TravellingPoint;
import com.simibubi.create.content.trains.graph.TrackNode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import purplecreate.tramways.mixinInterfaces.ITemporarySpeedLimitTrain;

@Mixin(value = Train.class, remap = false)
public class TrainMixin implements ITemporarySpeedLimitTrain {
  @Shadow public double throttle;
  @Unique private Double tempSpeedLimit$actual;

  @Inject(method = "frontSignalListener", at = @At("RETURN"), cancellable = true)
  private void tramways$frontSignalListener(CallbackInfoReturnable<TravellingPoint.IEdgePointListener> cir) {
    TravellingPoint.IEdgePointListener originalListener = cir.getReturnValue();
    cir.setReturnValue((distance, couple) -> {
      if (couple.getFirst() instanceof TramSignPoint sign) {
        TrackNode node = couple.getSecond().getSecond();
        sign.updateTrain((Train)(Object)this, node, 0);
        return false;
      }

      return originalListener.test(distance, couple);
    });
  }

  @Inject(method = "backSignalListener", at = @At("RETURN"), cancellable = true)
  private void tramways$backSignalListener(CallbackInfoReturnable<TravellingPoint.IEdgePointListener> cir) {
    TravellingPoint.IEdgePointListener originalListener = cir.getReturnValue();
    cir.setReturnValue((distance, couple) -> {
      if (couple.getFirst() instanceof TramSignPoint sign) {
        TrackNode node = couple.getSecond().getSecond();
        sign.removeTrain((Train)(Object)this, node);
        return false;
      }

      return originalListener.test(distance, couple);
    });
  }

  @Override
  public void tempSpeedLimit$set(double limit, boolean manual) {
    if (tempSpeedLimit$actual == null) {
      tempSpeedLimit$actual = throttle;
    }

    if (!manual) {
      throttle = limit;
    }
  }

  @Override
  public void tempSpeedLimit$restore(boolean manual) {
    if (tempSpeedLimit$actual != null && !manual) {
      throttle = tempSpeedLimit$actual;
    }

    tempSpeedLimit$actual = null;
  }

  @Override
  public boolean tempSpeedLimit$has() {
    return tempSpeedLimit$actual != null;
  }
}
