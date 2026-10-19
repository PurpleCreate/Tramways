package purplecreate.tramways.content.announcements.config;

import com.simibubi.create.content.trains.schedule.ScheduleRuntime;
import de.mrjulsen.crn.api.core.snapshot.StopSnapshot;
import de.mrjulsen.crn.api.core.snapshot.BoardEntry;
import purplecreate.tramways.content.announcements.train.TrainEntry;
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

  public static String replace(TrainEntry data, String input) {
    return replace(input, variable -> {
      boolean stopped = data.getTrain().runtime.state != ScheduleRuntime.State.IN_TRANSIT;

      List<StopSnapshot> service = data.journeySnapshot().stops();
      List<StopSnapshot> stopovers = data.getStopovers();

      if (variable.equals("via")) {
        return stopovers.stream().reduce("", (a, b) -> a + ", " + b.station().displayName(), (a, b) -> a + ", " + b);
      } else if (variable.equals("line")) {
        return data.getTrainDisplayName();
      } else if (variable.equals("carriages")) {
        return "" + data.trainSnapshot().carriageCount();
      } else if (variable.startsWith("origin.")) {
        return VariableManagerAccessor.tramways$handleStopover(service, 0, variable.substring(7));
      } else if (variable.startsWith("destination.")) {
        return VariableManagerAccessor.tramways$handleStopover(service, service.size() - 1, variable.substring(12));
      } else if (variable.startsWith("next.")) {
        int i = stopped ? 1 : 0;
        if (data.getStopovers().size() == i)
          return VariableManagerAccessor.tramways$handleStopover(service, service.size() - 1, variable.substring(5));
        return VariableManagerAccessor.tramways$handleStopover(stopovers, i, variable.substring(5));
      } else if (variable.startsWith("this.")) {
        if (!stopped)
          return "";
        if (data.getStopovers().isEmpty())
          return VariableManagerAccessor.tramways$handleStopover(service, service.size() - 1, variable.substring(5));
        return VariableManagerAccessor.tramways$handleStopover(stopovers, 0, variable.substring(5));
      }

      return null;
    });
  }

  public static List<String> iterate(TrainEntry data, String key, String input) {
    if ("via".equals(key)) {
      return data
        .getStopovers()
        .stream()
        .map(v -> replace(data, input.replaceAll("%x%", v.station().displayName())))
        .toList();
    }

    return List.of(replace(data, input));
  }

  public static String replace(BoardEntry data, String input) {
    return replace(input, variable ->
      VariableManagerAccessor.tramways$handleTrainEntry(data, variable)
    );
  }

  public static List<String> iterate(BoardEntry data, String key, String input) {
    if ("via".equals(key)) {
      return data
        .stopovers()
        .stream()
        .map(v -> replace(data, input.replaceAll("%x%", v.displayName())))
        .toList();
    }

    return List.of(replace(data, input));
  }
}
