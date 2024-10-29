package purplecreate.tramways.content.announcements;

import purplecreate.tramways.TNetworking;
import purplecreate.tramways.Tramways;
import purplecreate.tramways.config.TrainMessageType;
import purplecreate.tramways.content.announcements.info.TrainInfo;
import purplecreate.tramways.content.announcements.network.PlayMovingVoiceS2CPacket;
import purplecreate.tramways.util.MovementBehaviourUtil;
import com.simibubi.create.content.contraptions.behaviour.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.trains.schedule.ScheduleRuntime;

import java.util.Map;
import java.util.regex.Pattern;

public class SpeakerMovementBehaviour implements MovementBehaviour {
  @Override
  public void tick(MovementContext context) {
    if (context.world.isClientSide) return;

    MovementBehaviourUtil.withCarriage(context, (carriage) -> {
      TrainInfo trainInfo = TrainInfo.fromTrain(carriage.train);
      Map<String, String> props = trainInfo.getProperties();
      TrainMessageType type;

      boolean atStation = carriage.train.runtime.state == ScheduleRuntime.State.POST_TRANSIT;
      boolean atEnd = props.containsKey("end") && props.get("end").equals(props.get("current"));
      boolean approachingEnd = props.containsKey("end") && props.get("end").equals(props.get("next"));

      if (atStation) {
        if (atEnd)
          type = TrainMessageType.AT_TERMINUS;
        else if (approachingEnd)
          type = TrainMessageType.PRE_TERMINUS_AT_STATION;
        else
          type = TrainMessageType.AT_STATION;
      } else {
        if (approachingEnd)
          type = TrainMessageType.PRE_TERMINUS_AFTER_STATION;
        else
          type = TrainMessageType.AFTER_STATION;
      }

      String announcement = Pattern
        .compile("\\$([a-z_]+)")
        .matcher(trainInfo.getString(type))
        .replaceAll((result) -> props.getOrDefault(result.group(1), ""));

      if (!announcement.equals(context.temporaryData)) {
        context.temporaryData = announcement;

        TNetworking.sendToNear(
          new PlayMovingVoiceS2CPacket(
            trainInfo.getAnnouncer(),
            announcement,
            context.localPos,
            carriage
          ),
          context.position,
          50,
          context.world.dimension()
        );

        Tramways.LOGGER.debug("Sent play voice packet");
      }
    });
  }
}
