package purplecreate.tramways;

import com.simibubi.create.content.trains.graph.EdgePointType;

import com.simibubi.create.content.trains.schedule.destination.ScheduleInstruction;
import com.simibubi.create.foundation.utility.Pair;
import purplecreate.tramways.compat.Mods;
import purplecreate.tramways.compat.createrailwaysnavigator.CRNTrainInfo;
import purplecreate.tramways.content.announcements.info.TrainInfo;
import purplecreate.tramways.content.requestStop.train.RequestStopInstruction;
import purplecreate.tramways.content.signs.TramSignPoint;
import purplecreate.tramways.content.signs.demands.*;
import purplecreate.tramways.content.signs.schedule.SetPrimaryLimitInstruction;

import java.util.function.Supplier;

import static com.simibubi.create.content.trains.schedule.Schedule.INSTRUCTION_TYPES;

public class TExtras {
  public static class Schedule {
    private static void registerInstruction(String path, Supplier<? extends ScheduleInstruction> factory) {
      INSTRUCTION_TYPES.add(Pair.of(Tramways.rl(path), factory));
    }

    public static void register() {
      registerInstruction("request_stop", RequestStopInstruction::new);
      registerInstruction("set_primary_limit", SetPrimaryLimitInstruction::new);
    }
  }

  public static class EdgePointTypes {
    public static final EdgePointType<TramSignPoint> TRAM_SIGN =
      EdgePointType.register(Tramways.rl("tram_sign"), TramSignPoint::new);

    public static void register() {}
  }

  public static class SignDemands {
    public static void register() {
      SignDemand.register(Tramways.rl("speed"), new SpeedSignDemand());
      SignDemand.register(Tramways.rl("whistle"), new WhistleSignDemand());
      SignDemand.register(Tramways.rl("temporary_speed"), new TemporarySpeedSignDemand());
      SignDemand.register(Tramways.rl("temporary_end"), new TemporaryEndSignDemand());

      SignDemand.register(Tramways.rl("arrow_aux"), new ArrowAuxSignDemand());
      SignDemand.register(Tramways.rl("advance_warning_aux"), new AdvanceWarningAuxSignDemand());
    }
  }

  // runs in Tramways#commonSetup
  public static void registerCommon() {
    Schedule.register();
    EdgePointTypes.register();
    SignDemands.register();

    Mods.CREATERAILWAYSNAVIGATOR.ifLoadedRun(() -> () ->
      TrainInfo.registerPropertyGetter("crn", new CRNTrainInfo())
    );
  }
}
