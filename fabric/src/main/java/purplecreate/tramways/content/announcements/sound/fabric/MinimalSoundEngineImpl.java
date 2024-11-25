package purplecreate.tramways.content.announcements.sound.fabric;

import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.AudioStream;

import java.util.concurrent.CompletableFuture;

public class MinimalSoundEngineImpl {
  public static CompletableFuture<AudioStream> getStream(SoundInstance instance) {
    return instance.getAudioStream(null, null, false);
  }
}
