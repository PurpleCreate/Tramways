package purplecreate.tramways.config;

import com.google.gson.*;
import purplecreate.tramways.Tramways;

import java.io.*;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class Config {
  public static final Gson gson =
    new GsonBuilder()
      .setPrettyPrinting()
      .serializeNulls()
      .create();

  private static final File file = new File("config", Tramways.ID + "-config.json");

  private Map<String, TrainConfig> trains = null;
  private Map<String, StationConfig> stations = null;

  private <T> T find(Supplier<T> create, Map<String, T> map, String name) {
    if (!Objects.isNull(map))
      for (Map.Entry<String, T> entry : map.entrySet()) {
        String filter = entry.getKey();
        String regex = filter.isBlank()
          ? filter
          : "\\Q" + filter.replace("*", "\\E.*\\Q") + "\\E";

        if (name.matches(regex))
          return entry.getValue();
      }

    return create.get();
  }

  public TrainConfig findTrain(String trainName) {
    return find(TrainConfig::new, trains, trainName);
  }

  public StationConfig findStation(String stationName) {
    return find(StationConfig::new, stations, stationName);
  }

  public void write() throws IOException {
    if (!file.exists())
      file.createNewFile();

    try (FileWriter writer = new FileWriter(file)) {
      gson.toJson(this, writer);
    }
  }

  public static Config read() {
    try (FileReader reader = new FileReader(file)) {
      return gson.fromJson(reader, Config.class);
    } catch (IOException e) {
      return new Config();
    }
  }
}
