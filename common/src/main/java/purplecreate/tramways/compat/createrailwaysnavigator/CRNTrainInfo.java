package purplecreate.tramways.compat.createrailwaysnavigator;

import com.simibubi.create.content.trains.entity.Train;
import de.mrjulsen.crn.data.TrainGroup;
import de.mrjulsen.crn.data.TrainInfo;
import de.mrjulsen.crn.data.TrainLine;
import de.mrjulsen.crn.data.train.TrainData;
import de.mrjulsen.crn.data.train.TrainListener;
import de.mrjulsen.crn.data.train.TrainPrediction;
import de.mrjulsen.crn.util.ModUtils;
import purplecreate.tramways.content.announcements.info.TrainInfo.PropertyGetter;

import java.util.HashMap;
import java.util.Map;

public class CRNTrainInfo implements PropertyGetter {
  @Override
  public Map<String, String> getProperties(Train train) {
    Map<String, String> props = new HashMap<>();
    TrainData trainData = TrainListener.data.get(train.id);

    if (trainData != null) {
      TrainInfo trainInfo = trainData.getTrainInfo(train.runtime.currentEntry);
      TrainPrediction trainPrediction = trainData.getNextStopPrediction().orElse(null);

      TrainGroup group = trainInfo.group();
      TrainLine line = trainInfo.line();

      props.put("group", group == null ? "" : group.getGroupName());
      props.put("line", line == null ? "" : line.getLineName());
      props.put("arrival_time", trainPrediction == null ? "" : ModUtils.formatTime(trainPrediction.getScheduledArrivalTime(), false));
      props.put("departure_time", trainPrediction == null ? "" : ModUtils.formatTime(trainPrediction.getScheduledDepartureTime(), false));
    }

    return props;
  }
}
