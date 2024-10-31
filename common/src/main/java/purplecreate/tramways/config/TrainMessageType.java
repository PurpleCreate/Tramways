package purplecreate.tramways.config;

public enum TrainMessageType {
  AT_STATION("This is, $current. This tram is for, $end. The next stop is, $next, $next_extra."),
  AT_TERMINUS("This is, $current. This tram terminates here."),
  AFTER_STATION("This tram is for, $end. The next stop is, $next, $next_extra."),
  PRE_TERMINUS_AT_STATION("This is, $current. The next stop is, $next, $next_extra, which is the last stop."),
  PRE_TERMINUS_AFTER_STATION("The next stop is, $next, $next_extra, which is the last stop.");

  public final String defaultString;

  TrainMessageType(String defaultString) {
    this.defaultString = defaultString;
  }
}
