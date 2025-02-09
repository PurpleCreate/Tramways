package purplecreate.tramways.config;

import java.util.*;

public class StationConfig {
  private String alias = null;
  private String extra = null;
  private String announcer = null;
  private Map<StationMessageType, List<MessageConfig>> messages = null;

  public static StationConfig getInitial() {
    StationConfig stationConfig = new StationConfig();

    stationConfig.announcer = stationConfig.getAnnouncer();
    stationConfig.messages = new HashMap<>();

    for (StationMessageType type : StationMessageType.class.getEnumConstants())
      stationConfig.messages.put(type, stationConfig.getMessages(type));

    return stationConfig;
  }

  public String getAlias() {
    return alias;
  }

  public String getExtra() {
    return extra;
  }

  public String getAnnouncer() {
    if (Objects.isNull(announcer))
      return "en-GB-SoniaNeural";
    return announcer;
  }

  public List<MessageConfig> getMessages(StationMessageType type) {
    if (Objects.isNull(messages))
      return List.of(MessageConfig.simple(type.defaultString));
    List<MessageConfig> msgs = messages.getOrDefault(type, List.of());
    if (msgs.isEmpty())
      return List.of(MessageConfig.simple(type.defaultString));
    return msgs;
  }

  public MessageConfig getRandomMessage(StationMessageType type) {
    List<MessageConfig> msgs = getMessages(type);
    return msgs.get(new Random().nextInt(msgs.size()));
  }
}
