package purplecreate.tramways.content.announcements.config;

import com.simibubi.create.Create;
import com.simibubi.create.content.trains.schedule.ScheduleRuntime;
import de.mrjulsen.crn.data.train.ETrainStopState;
import de.mrjulsen.crn.data.train.portable.StationDisplayData;
import de.mrjulsen.crn.data.train.portable.TrainDisplayData;
import purplecreate.tramways.mixins.VariableManagerAccessor;

import java.util.List;
import java.util.function.Function;

public class Variables {
  public static String replace(String input, Function<String, String> replacementGetter) {
    StringBuilder output = new StringBuilder();

    for (int i = 0; i < input.length(); i++) {
      char c = input.charAt(i);

      if (c == '\\' && i + 1 < input.length() && input.charAt(i + 1) == '%') {
        output.append('%');
        i++;
        continue;
      }

      if (c == '%') {
        int end = input.indexOf('%', i + 1);
        if (end != -1) {
          String variable = input.substring(i + 1, end);
          String replacement = replacementGetter.apply(variable);
          if (replacement != null) {
            output.append(replacement);
          } else {
            output.append('%').append(variable).append('%');
          }
          i = end;
          continue;
        }
      }

      output.append(c);
    }

    return output.toString();
  }

  public static String replace(TrainDisplayData data, String input) {
    return replace(input, variable -> {
      boolean stopped = Create.RAILWAYS.trains.get(data.getTrainData().getId()).runtime.state != ScheduleRuntime.State.IN_TRANSIT;

      if (variable.equals("via")) {
        return data.getStopovers().stream().reduce("", (a, b) -> a + ", " + b.getRealTimeStation().tagName(), (a, b) -> a + ", " + b);
      } else if (variable.equals("line")) {
        return data.getTrainData().getName(ETrainStopState.beforeArrival(!data.isWaitingAtStation()));
      } else if (variable.equals("carriages")) {
        return "" + data.getTrainData().getCarriages();
      } else if (variable.startsWith("origin.")) {
        return VariableManagerAccessor.tramways$handleStopover(data.getAllStops(), 0, variable.substring(7));
      } else if (variable.startsWith("destination.")) {
        return VariableManagerAccessor.tramways$handleStopover(data.getAllStops(), data.getAllStops().size() - 1, variable.substring(12));
      } else if (variable.startsWith("next.")) {
        int i = stopped ? 1 : 0;
        if (data.getStopovers().size() == i)
          return VariableManagerAccessor.tramways$handleStopover(data.getAllStops(), data.getAllStops().size() - 1, variable.substring(5));
        return VariableManagerAccessor.tramways$handleStopover(data.getStopovers(), i, variable.substring(5));
      } else if (variable.startsWith("this.")) {
        if (!stopped)
          return "";
        if (data.getStopovers().isEmpty())
          return VariableManagerAccessor.tramways$handleStopover(data.getAllStops(), data.getAllStops().size() - 1, variable.substring(5));
        return VariableManagerAccessor.tramways$handleStopover(data.getStopovers(), 0, variable.substring(5));
      }

      return null;
    });
  }

  public static List<String> iterate(TrainDisplayData data, String key, String input) {
    if ("via".equals(key)) {
      return data
        .getStopovers()
        .stream()
        .map(v -> replace(data, input.replaceAll("%x%", v.getRealTimeStation().tagName())))
        .toList();
    }

    return List.of(replace(data, input));
  }

  public static String replace(StationDisplayData data, String input) {
    return replace(input, variable ->
      VariableManagerAccessor.tramways$handleTrainEntry(data, variable)
    );
  }

  public static List<String> iterate(StationDisplayData data, String key, String input) {
    if ("via".equals(key)) {
      return data
        .getStopovers()
        .stream()
        .map(v -> replace(data, input.replaceAll("%x%", v)))
        .toList();
    }

    return List.of(replace(data, input));
  }
}
