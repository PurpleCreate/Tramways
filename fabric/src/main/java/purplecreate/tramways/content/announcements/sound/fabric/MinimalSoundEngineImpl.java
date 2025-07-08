package purplecreate.tramways.content.announcements.sound.fabric;

import net.fabricmc.fabric.api.client.sound.v1.FabricSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.AudioStream;

import java.util.concurrent.CompletableFuture;

public class MinimalSoundEngineImpl {
  public static CompletableFuture<AudioStream> getStream(SoundInstance instance) {
    if (instance instanceof FabricSoundInstance fsi) {
      return fsi.getAudioStream(null, null, false);
    } else {
      throw new RuntimeException("Expected a FabricSoundInstance");
    }
  }
}
