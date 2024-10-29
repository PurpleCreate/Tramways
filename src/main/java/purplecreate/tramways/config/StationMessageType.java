package purplecreate.tramways.config;

public enum StationMessageType {
  WITHOUT_PLATFORM("The next train to arrive will be a $train_name service to $destination."),
  WITH_PLATFORM("The next train to arrive at platform $platform will be a $train_name service to $destination.");

  public final String defaultString;

  StationMessageType(String defaultString) {
    this.defaultString = defaultString;
  }
}
