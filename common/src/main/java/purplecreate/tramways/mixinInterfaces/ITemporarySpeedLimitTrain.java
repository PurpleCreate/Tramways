package purplecreate.tramways.mixinInterfaces;

public interface ITemporarySpeedLimitTrain {
  void tempSpeedLimit$set(double limit, boolean manual);
  void tempSpeedLimit$restore(boolean manual);
  boolean tempSpeedLimit$has();
}
