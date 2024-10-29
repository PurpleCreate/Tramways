package purplecreate.tramways;

import com.simibubi.create.foundation.ponder.PonderRegistrationHelper;
import purplecreate.tramways.compat.Mods;
import purplecreate.tramways.ponder.SpeakerScenes;
import purplecreate.tramways.ponder.TramSignScenes;
import purplecreate.tramways.ponder.TramSignalScenes;

public class TPonders {
  private static final PonderRegistrationHelper HELPER = new PonderRegistrationHelper(Tramways.ID);

  public static void register() {
    HELPER
      .forComponents(TBlocks.TRAM_SIGNAL)
      .addStoryBoard("tram_signal/track_signals", TramSignalScenes::trackSignals);

    if (Mods.RAILWAYS.loaded())
      HELPER
        .forComponents(TBlocks.TRAM_SIGNAL)
        .addStoryBoard("tram_signal/track_switches", TramSignalScenes::trackSwitches);

    HELPER
      .forComponents(TBlocks.TRAM_SIGN, TBlocks.RAILWAY_SIGN)
      .addStoryBoard("tram_sign/signs", TramSignScenes::placingSigns)
      .addStoryBoard("tram_sign/signs", TramSignScenes::signDemandSpeedLimit)
      .addStoryBoard("tram_sign/signs", TramSignScenes::signDemandWhistle)
      .addStoryBoard("tram_sign/signs", TramSignScenes::signDemandTemporaryLimits);

    HELPER
      .forComponents(TBlocks.SPEAKER)
      .addStoryBoard("speaker/train", SpeakerScenes::train)
      .addStoryBoard("speaker/station", SpeakerScenes::station);
  }
}
