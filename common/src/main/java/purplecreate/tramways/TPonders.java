package purplecreate.tramways;

import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import purplecreate.tramways.ponder.SpeakerScenes;
import purplecreate.tramways.ponder.TramSignScenes;
import purplecreate.tramways.ponder.TramSignalScenes;

public class TPonders implements PonderPlugin {
  @Override
  public String getModId() {
    return Tramways.ID;
  }

  @Override
  public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
    PonderSceneRegistrationHelper<ItemProviderEntry<?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);

    HELPER
      .forComponents(TBlocks.TRAM_SIGNAL)
      .addStoryBoard("tram_signal/track_signals", TramSignalScenes::trackSignals);

    /*if (Mods.RAILWAYS.loaded())
      HELPER
        .forComponents(TBlocks.TRAM_SIGNAL)
        .addStoryBoard("tram_signal/track_switches", TramSignalScenes::trackSwitches);*/

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
