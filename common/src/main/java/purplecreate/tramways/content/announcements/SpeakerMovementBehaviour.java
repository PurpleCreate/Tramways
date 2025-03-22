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

public class SpeakerMovementBehaviour implements MovementBehaviour {
  @Override
  public void tick(MovementContext context) {
    if (context.world.isClientSide) return;

    MovementBehaviourUtil.withCarriage(context, (carriage) -> {
      ScheduleRuntime rt = carriage.train.runtime;

      if (rt.state == ScheduleRuntime.State.PRE_TRANSIT)
        return;

      TrainInfo trainInfo = TrainInfo.fromTrain(carriage.train);
      Map<String, String> props = trainInfo.getProperties();
      TrainMessageType type;

      boolean atStation = rt.state == ScheduleRuntime.State.POST_TRANSIT;
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

      String announcementConditions =
        carriage.train.runtime.state.name() + "," +
          props.getOrDefault("current", "") + "," +
          props.getOrDefault("next", "") + "," +
          props.getOrDefault("end", "");

      int previousInstruction = rt.currentEntry == 0
        ? rt.getSchedule().entries.size() - 1
        : rt.currentEntry - 1;

      if (
        !announcementConditions.equals(context.temporaryData)
          && !trainInfo.travellingToWaypoint(atStation ? rt.currentEntry : previousInstruction)
      ) {
        context.temporaryData = announcementConditions;

        TNetworking.sendToNear(
          new PlayMovingVoiceS2CPacket(
            trainInfo.getAnnouncer(),
            trainInfo
              .getString(type)
              .applyProperties(props),
            context.localPos,
            carriage
          ),
          context.position,
          50,
          context.world.dimension()
        );
      }
    });
  }
}
