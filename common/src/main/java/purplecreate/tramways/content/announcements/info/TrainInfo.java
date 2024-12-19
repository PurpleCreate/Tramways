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

  private List<StationInfo> getCallingAt() {
    return getCallingAt(train.runtime.currentEntry);
  }

  private List<StationInfo> getCallingAt(int startAt) {
    ScheduleRuntime rt = train.runtime;
    Schedule schedule = rt.getSchedule();
    List<StationInfo> callingAt = new ArrayList<>();

    if (schedule == null) return callingAt;

    List<ScheduleEntry> entries = new ArrayList<>(schedule.entries);
    if (schedule.cyclic)
      entries.add(ListUtil.getFirst(entries, (e) -> e.instruction instanceof DestinationInstruction));

    for (int i = startAt; i < entries.size(); i++) {
      if (entries.get(i).instruction instanceof DestinationInstruction instruction) {
        StationInfo destination = StationInfo.fromFilter(instruction.getFilter());
        if (
          schedule.cyclic
            && callingAt.stream().anyMatch((e) -> Objects.equals(e.getAlias(), destination.getAlias()))
        ) break;
        callingAt.add(destination);
      }
    }

    return callingAt;
  }

  private Couple<StationInfo> getTermini() {
    List<StationInfo> callingAt = getCallingAt(0);
    if (callingAt.isEmpty()) return Couple.create(null, null);
    return Couple.create(callingAt.get(0), callingAt.get(callingAt.size() - 1));
  }

  public String getAnnouncer() {
    return Config
      .getInstance()
      .findTrain(train.name.getString())
      .getAnnouncer();
  }

  public String getString(TrainMessageType type) {
    return Config
      .getInstance()
      .findTrain(train.name.getString())
      .getRandomMessage(type);
  }

  public Map<String, String> getProperties() {
    return getProperties(true);
  }

  public Map<String, String> getProperties(boolean fixEnd) {
    List<StationInfo> callingAt = getCallingAt();
    Map<String, String> props = new HashMap<>();

    props.put("train_name", train.name.getString());
    props.put("train_length", "" + train.carriages.size());

    if (callingAt.isEmpty()) return props;

    StationInfo end = callingAt.get(callingAt.size() - 1);
    if (fixEnd)
      for (StationInfo terminus : getTermini()) {
        if (Objects.equals(callingAt.get(0).getAlias(), terminus.getAlias())) {
          end = terminus;
          break;
        }
      }
    props.put("end", end.getAlias());
    props.put("end_extra", end.getExtra());

    if (train.runtime.state == ScheduleRuntime.State.POST_TRANSIT) {
      StationInfo current = callingAt.get(0);
      props.put("current", current.getAlias());
      props.put("current_extra", current.getExtra());

      StationInfo next = callingAt.get(callingAt.size() > 1 ? 1 : 0);
      props.put("next", next.getAlias());
      props.put("next_extra", next.getExtra());
    } else {
      StationInfo next = callingAt.get(0);
      props.put("next", next.getAlias());
      props.put("next_extra", next.getExtra());
    }

    return props;
  }
}
