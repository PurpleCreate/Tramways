package purplecreate.tramways.content.announcements;

import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTarget;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import com.simibubi.create.content.trains.display.GlobalTrainDisplayData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import purplecreate.tramways.TNetworking;
import purplecreate.tramways.Tramways;
import purplecreate.tramways.config.StationMessageType;
import purplecreate.tramways.content.announcements.info.StationInfo;
import purplecreate.tramways.content.announcements.network.PlayVoiceS2CPacket;

import java.util.*;
import java.util.regex.Pattern;

public class SpeakerDisplayTarget extends DisplayTarget {
  private static final int windowMin = 200;
  private static final int windowMax = 900;
  private final Set<UUID> announced = new HashSet<>();

  private static String getPlatform(String filter, GlobalTrainDisplayData.TrainDeparturePrediction prediction) {
    String platform = "";

    if (filter.contains("*")) {
      platform = prediction.destination;
      for (String string : filter.split("\\*"))
        if (!string.isEmpty())
          platform = platform.replace(string, "");
    }

    return platform;
  }

  @Override
  public void acceptText(int i, List<MutableComponent> list, DisplayLinkContext context) {
    CompoundTag config = context.sourceConfig();
    String id = config.getString("Id");
    String filter = config.getString("Filter");

    if (id.equals("create:track_station_source_station_summary")) {
      List<GlobalTrainDisplayData.TrainDeparturePrediction> predictions =
        GlobalTrainDisplayData.prepare(filter, 5);

      for (GlobalTrainDisplayData.TrainDeparturePrediction prediction : predictions) {

        if (
          !announced.contains(prediction.train.id)
            && !prediction.destination.contains("*")
            && prediction.ticks >= windowMin
            && prediction.ticks <= windowMax
        ) {
          announced.add(prediction.train.id);

          StationInfo stationInfo = StationInfo.fromFilter(filter);
          Map<String, String> props = stationInfo.getProperties(
            prediction.train,
            getPlatform(filter, prediction)
          );
          StationMessageType type = filter.contains("*")
            ? StationMessageType.WITH_PLATFORM
            : StationMessageType.WITHOUT_PLATFORM;

          String announcement = Pattern
            .compile("\\$([a-z_:]+)")
            .matcher(stationInfo.getString(type))
            .replaceAll((result) -> props.getOrDefault(result.group(1), ""));

          TNetworking.sendToNear(
            new PlayVoiceS2CPacket(
              stationInfo.getAnnouncer(),
              announcement,
              context.getTargetPos()
            ),
            context.getTargetPos().getCenter(),
            50,
            context.level().dimension()
          );
        } else if (prediction.ticks < windowMin) {
          announced.remove(prediction.train.id);
        }
      }
    }
  }

  @Override
  public DisplayTargetStats provideStats(DisplayLinkContext displayLinkContext) {
    return new DisplayTargetStats(1, 1, this);
  }

  @Override
  public Component getLineOptionText(int line) {
    return Tramways.translatable("display_target.speaker");
  }
}
