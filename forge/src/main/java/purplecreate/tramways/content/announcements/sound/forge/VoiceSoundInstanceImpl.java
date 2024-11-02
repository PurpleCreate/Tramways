package purplecreate.tramways.content.announcements.sound.forge;

import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.client.sounds.SoundBufferLibrary;
import net.minecraft.core.BlockPos;
import purplecreate.tramways.content.announcements.sound.VoiceSoundInstance;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

public class VoiceSoundInstanceImpl extends VoiceSoundInstance {
  protected VoiceSoundInstanceImpl(InputStream stream, BlockPos pos) {
    super(stream, pos);
  }

  public static VoiceSoundInstance create(InputStream stream, BlockPos pos) {
    return new VoiceSoundInstanceImpl(stream, pos);
  }

  @Override
  public CompletableFuture<AudioStream> getStream(SoundBufferLibrary soundBuffers, Sound sound, boolean looping) {
    return getStreamInternal(stream);
  }
}
