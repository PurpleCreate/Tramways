package purplecreate.tramways.mixinInterfaces;

public interface ISpeedLimitableTrain {
  void tempSpeedLimit$set(double limit, boolean manual);
  void tempSpeedLimit$updateActual(double throttle);
  void tempSpeedLimit$restore(boolean manual);
  boolean tempSpeedLimit$has();

  double primaryLimit$get();
  void primaryLimit$set(double limit);
}
