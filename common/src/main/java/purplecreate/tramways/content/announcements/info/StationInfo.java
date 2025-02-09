package purplecreate.tramways.content.announcements.info;

import com.simibubi.create.content.trains.entity.Train;
import purplecreate.tramways.config.Config;
import purplecreate.tramways.config.MessageConfig;
import purplecreate.tramways.config.StationMessageType;

import java.util.Map;
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
      .getInstance()
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
      .getInstance()
      .findStation(filter)
      .getExtra();

    return Objects.isNull(extra)
      ? ""
      : extra;
  }

  public String getAnnouncer() {
    return Config
      .getInstance()
      .findStation(filter)
      .getAnnouncer();
  }

  public MessageConfig getString(StationMessageType type) {
    return Config
      .getInstance()
      .findStation(filter)
      .getRandomMessage(type);
  }

  public Map<String, String> getProperties(Train train, String platform) {
    TrainInfo trainInfo = TrainInfo.fromTrain(train);
    Map<String, String> props = trainInfo.getProperties(false);

    if (props.containsKey("end"))
      props.put("destination", props.get("end"));

    props.put("platform", platform);

    return props;
  }
}
