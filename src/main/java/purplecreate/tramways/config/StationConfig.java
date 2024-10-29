package purplecreate.tramways.config;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class StationConfig {
  private String alias = null;
  private String extra = null;
  private String announcer = null;
  private Map<StationMessageType, List<String>> messages = null;

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

  public List<String> getMessages(StationMessageType type) {
    if (Objects.isNull(messages))
      return List.of(type.defaultString);
    List<String> msgs = messages.getOrDefault(type, List.of());
    if (msgs.isEmpty())
      return List.of(type.defaultString);
    return msgs;
  }

  public String getRandomMessage(StationMessageType type) {
    List<String> msgs = getMessages(type);
    return msgs.get(new Random().nextInt(msgs.size()));
  }
}
