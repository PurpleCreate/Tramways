package purplecreate.tramways.content.announcements.sound.forge;

import com.simibubi.create.content.trains.entity.Carriage;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.client.sounds.SoundBufferLibrary;
import net.minecraft.core.BlockPos;
import purplecreate.tramways.content.announcements.sound.MovingVoiceSoundInstance;
import purplecreate.tramways.content.announcements.sound.VoiceSoundInstance;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

public class MovingVoiceSoundInstanceImpl extends MovingVoiceSoundInstance {
  protected MovingVoiceSoundInstanceImpl(InputStream stream, Carriage carriage, BlockPos localPos) {
    super(stream, carriage, localPos);
  }

  public static MovingVoiceSoundInstance create(InputStream stream, Carriage carriage, BlockPos localPos) {
    return new MovingVoiceSoundInstanceImpl(stream, carriage, localPos);
  }

  @Override
  public CompletableFuture<AudioStream> getStream(SoundBufferLibrary soundBuffers, Sound sound, boolean looping) {
    return VoiceSoundInstance.getStreamInternal(stream);
  }
}
