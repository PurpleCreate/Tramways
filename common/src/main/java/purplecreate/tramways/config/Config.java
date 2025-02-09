package purplecreate.tramways.config;

import com.google.gson.*;
import purplecreate.tramways.Tramways;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class Config {
  private static Config instance;

  public static final Gson gson =
    new GsonBuilder()
      .setPrettyPrinting()
      .serializeNulls()
      .registerTypeAdapter(MessageConfig.class, new MessageConfig())
      .create();

  private static final File file = new File("config", Tramways.ID + "-config.json");

  private Map<String, TrainConfig> trains = null;
  private Map<String, StationConfig> stations = null;

  public static Config getInstance() {
    if (instance == null)
      return reload();
    return instance;
  }

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

  public static Config reload() {
    Config config;

    try (FileReader reader = new FileReader(file)) {
      config = gson.fromJson(reader, Config.class);

      if (config == null)
        config = new Config();
    } catch (IOException e) {
      config = new Config();
    }

    // generate initial config
    if (config.trains == null) {
      config.trains = new HashMap<>();
      config.trains.put("*", TrainConfig.getInitial());
    }

    if (config.stations == null) {
      config.stations = new HashMap<>();
      config.stations.put("*", StationConfig.getInitial());
    }

    instance = config;
    return config;
  }
}
