package purplecreate.tramways.compat.createrailwaysnavigator;

import com.simibubi.create.content.trains.entity.Train;
import de.mrjulsen.crn.data.TrainCategory;
import de.mrjulsen.crn.data.TrainInfo;
import de.mrjulsen.crn.data.TrainLine;
import de.mrjulsen.crn.data.train.TrainData;
import de.mrjulsen.crn.data.train.TrainListener;
import de.mrjulsen.crn.data.train.TrainPrediction;
import de.mrjulsen.mcdragonlib.util.TimeUtils;
import purplecreate.tramways.content.announcements.info.TrainInfo.PropertyGetter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CRNTrainInfo implements PropertyGetter {
  @Override
  public Map<String, String> getProperties(Train train) {
    Map<String, String> props = new HashMap<>();
    Optional<TrainData> _trainData = TrainListener.getTrainData(train);

    if (_trainData.isPresent()) {
      TrainData trainData = _trainData.get();

      TrainInfo trainInfo = trainData.getTrainInfo(train.runtime.currentEntry);
      TrainPrediction trainPrediction = trainData.getNextStopPrediction().orElse(null);

      TrainCategory group = trainInfo.category();
      TrainLine line = trainInfo.line();

      props.put("group", group == null ? "" : group.getCategoryName());
      props.put("line", line == null ? "" : line.getLineName());

      String arrivalTime = TimeUtils.formatTime(trainPrediction == null ? 0 : trainPrediction.scheduled().departureTime(), TimeUtils.TimeFormat.HOURS_24);
      String departureTime = TimeUtils.formatTime(trainPrediction == null ? 0 : trainPrediction.scheduled().departureTime(), TimeUtils.TimeFormat.HOURS_24);
      props.put("arrival_time", arrivalTime);
      props.put("departure_time", departureTime);
      props.put("arrival_time_hours", arrivalTime.split(":")[0]);
      props.put("arrival_time_mins", arrivalTime.split(":")[1]);
      props.put("departure_time_hours", departureTime.split(":")[0]);
      props.put("departure_time_mins", departureTime.split(":")[1]);
    }

    return props;
  }
}
