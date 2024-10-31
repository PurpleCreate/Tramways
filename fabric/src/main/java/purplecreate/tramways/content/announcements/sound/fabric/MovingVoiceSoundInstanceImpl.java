package purplecreate.tramways.content.announcements.sound.fabric;

import com.simibubi.create.content.trains.entity.Carriage;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.client.sounds.SoundBufferLibrary;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
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
  public CompletableFuture<AudioStream> getAudioStream(SoundBufferLibrary loader, ResourceLocation id, boolean repeatInstantly) {
    return VoiceSoundInstance.getStreamInternal(stream);
  }
}
