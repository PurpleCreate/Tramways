package purplecreate.tramways.content.announcements.info;

import purplecreate.tramways.config.Config;
import purplecreate.tramways.config.StationMessageType;

import java.util.Objects;

public class StationInfo {
  private final String filter;

  private StationInfo(String filter) {
    this.filter = filter;
  }

  public static StationInfo fromFilter(String filter) {
    return new StationInfo(filter);
  }

  public String getAlias() {
    String alias = Config
      .read()
      .findStation(filter)
      .getAlias();

    return Objects.isNull(alias)
      ? filter
          .replace("*", "")
          .replaceAll(" +", " ")
          .trim()
      : alias;
  }

  public String getExtra() {
    String extra = Config
      .read()
      .findStation(filter)
      .getExtra();

    return Objects.isNull(extra)
      ? ""
      : extra;
  }

  public String getAnnouncer() {
    return Config
      .read()
      .findStation(filter)
      .getAnnouncer();
  }

  public String getString(StationMessageType type) {
    return Config
      .read()
      .findStation(filter)
      .getRandomMessage(type);
  }
}
