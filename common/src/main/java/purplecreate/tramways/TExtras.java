package purplecreate.tramways;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.api.registry.CreateRegistries;
import com.simibubi.create.content.trains.graph.EdgePointType;

import com.simibubi.create.content.trains.schedule.destination.ScheduleInstruction;
import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.createmod.catnip.data.Pair;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import purplecreate.tramways.content.announcements.config.instructions.*;
import purplecreate.tramways.content.announcements.station.SpeakerDisplayTarget;
import purplecreate.tramways.content.announcements.station.StationSpeakerDisplaySource;
import purplecreate.tramways.content.announcements.train.ConfigureAnnouncementsInstruction;
import purplecreate.tramways.content.requestStop.train.RequestStopInstruction;
import purplecreate.tramways.content.signs.TramSignPoint;
import purplecreate.tramways.content.signs.demands.*;
import purplecreate.tramways.content.signs.schedule.SetPrimaryLimitInstruction;
import purplecreate.tramways.mixins.CreateAccessor;

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
      registerInstruction("configure_announcements", ConfigureAnnouncementsInstruction::new);
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

  public static class AnnouncementInstructions {
    public static void register() {
      AnnouncementInstruction.register(new PlayFileInstruction());
      AnnouncementInstruction.register(new PlayTTSInstruction());
      AnnouncementInstruction.register(new SetAnnouncerInstruction());
      AnnouncementInstruction.register(new PlayFileForEachInstruction());
    }
  }

  public static class DisplaySources {
    //public static final RegistryEntry<SignalDisplaySource> SIGNAL = Tramways.REGISTRATE.displaySource("signal", SignalDisplaySource::new).register();
    public static final RegistryEntry<StationSpeakerDisplaySource> STATION_SPEAKER = Tramways.REGISTRATE.displaySource("station_speaker", StationSpeakerDisplaySource::new).register();

    private static void checkAndAssign(Pair<Block, DisplaySource> pair) {
      if (pair.getFirst() == null || pair.getSecond() == null) return;
      DisplaySource.BY_BLOCK.add(pair.getFirst(), pair.getSecond());
    }

    private static void assign(AbstractRegistrate<?> blockReg, ResourceLocation blockId, AbstractRegistrate<?> sourceReg, ResourceLocation sourceId) {
      Pair<Block, DisplaySource> pair = Pair.of(null, null);

      if (BuiltInRegistries.BLOCK.containsKey(blockId)) {
        pair.setFirst(BuiltInRegistries.BLOCK.get(blockId));
      } else {
        blockReg.addRegisterCallback(blockId.getPath(), Registries.BLOCK, block -> {
          pair.setFirst(block);
          checkAndAssign(pair);
        });
      }

      if (CreateBuiltInRegistries.DISPLAY_SOURCE.containsKey(blockId)) {
        pair.setSecond(CreateBuiltInRegistries.DISPLAY_SOURCE.get(sourceId));
      } else {
        sourceReg.addRegisterCallback(sourceId.getPath(), CreateRegistries.DISPLAY_SOURCE, source -> {
          pair.setSecond(source);
          checkAndAssign(pair);
        });
      }
    }

    public static void register() {
      //assign(
      //  CreateAccessor.getRegistrate(), AllBlocks.TRACK_SIGNAL.getId(),
      //  Tramways.REGISTRATE, SIGNAL.getId()
      //);
      assign(
        CreateAccessor.getRegistrate(), AllBlocks.TRACK_STATION.getId(),
        Tramways.REGISTRATE, STATION_SPEAKER.getId()
      );
    }
  }

  public static class DisplayTargets {
    public static final RegistryEntry<SpeakerDisplayTarget> SPEAKER = Tramways.REGISTRATE.displayTarget("speaker", SpeakerDisplayTarget::new).register();

    public static void register() {}
  }

  // runs in Tramways#commonSetup
  public static void registerCommon() {
    Schedule.register();
    EdgePointTypes.register();
    SignDemands.register();
    AnnouncementInstructions.register();
  }
}
