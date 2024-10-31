package purplecreate.tramways.content.announcements.info;

import com.simibubi.create.foundation.utility.Couple;
import purplecreate.tramways.config.Config;
import purplecreate.tramways.config.TrainMessageType;
import purplecreate.tramways.util.ListUtil;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.schedule.Schedule;
import com.simibubi.create.content.trains.schedule.ScheduleEntry;
import com.simibubi.create.content.trains.schedule.ScheduleRuntime;
import com.simibubi.create.content.trains.schedule.destination.DestinationInstruction;

import java.util.*;

public class TrainInfo {
  private final Train train;

  private TrainInfo(Train train) {
    this.train = train;
  }

  public static TrainInfo fromTrain(Train train) {
    return new TrainInfo(train);
  }

  private List<String> getCallingAt() {
    return getCallingAt(train.runtime.currentEntry);
  }

  private List<String> getCallingAt(int startAt) {
    ScheduleRuntime rt = train.runtime;
    Schedule schedule = rt.getSchedule();
    List<String> callingAt = new ArrayList<>();

    if (schedule == null) return callingAt;

    List<ScheduleEntry> entries = new ArrayList<>(schedule.entries);
    if (schedule.cyclic)
      entries.add(ListUtil.getFirst(entries, (e) -> e.instruction instanceof DestinationInstruction));

    for (int i = startAt; i < entries.size(); i++) {
      if (entries.get(i).instruction instanceof DestinationInstruction instruction) {
        String destination = instruction.getFilter();
        if (schedule.cyclic && callingAt.contains(destination)) break;
        callingAt.add(destination);
      }
    }

    return callingAt;
  }

  private Couple<String> getTermini() {
    List<String> callingAt = getCallingAt(0);
    if (callingAt.isEmpty()) return Couple.create(null, null);
    return Couple.create(callingAt.get(0), callingAt.get(callingAt.size() - 1));
  }

  public String getAnnouncer() {
    return Config
      .read()
      .findTrain(train.name.getString())
      .getAnnouncer();
  }

  public String getString(TrainMessageType type) {
    return Config
      .read()
      .findTrain(train.name.getString())
      .getRandomMessage(type);
  }

  public Map<String, String> getProperties() {
    List<String> callingAt = getCallingAt();
    Map<String, String> props = new HashMap<>();

    props.put("train_name", train.name.getString());

    if (callingAt.isEmpty()) return props;

    StationInfo end = StationInfo.fromFilter(callingAt.get(callingAt.size() - 1));
    for (String terminus : getTermini()) {
      if (callingAt.get(0).equals(terminus)) {
        end = StationInfo.fromFilter(terminus);
        break;
      }
    }
    props.put("end", end.getAlias());
    props.put("end_extra", end.getExtra());

    if (train.runtime.state == ScheduleRuntime.State.POST_TRANSIT) {
      StationInfo current = StationInfo.fromFilter(callingAt.get(0));
      props.put("current", current.getAlias());
      props.put("current_extra", current.getExtra());

      StationInfo next = StationInfo.fromFilter(callingAt.get(1));
      props.put("next", next.getAlias());
      props.put("next_extra", next.getExtra());
    } else {
      StationInfo next = StationInfo.fromFilter(callingAt.get(0));
      props.put("next", next.getAlias());
      props.put("next_extra", next.getExtra());
    }

    return props;
  }
}
