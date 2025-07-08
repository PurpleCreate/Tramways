package purplecreate.tramways.content.announcements.sound.fabric;

import com.simibubi.create.content.trains.entity.Carriage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.sound.v1.FabricSoundInstance;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.client.sounds.SoundBufferLibrary;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import purplecreate.tramways.content.announcements.sound.MovingVoiceSoundInstance;

import java.util.concurrent.CompletableFuture;

@Environment(EnvType.CLIENT)
public class MovingVoiceSoundInstanceImpl extends MovingVoiceSoundInstance implements FabricSoundInstance {
  protected MovingVoiceSoundInstanceImpl(AudioStream stream, Carriage carriage, BlockPos localPos) {
    super(stream, carriage, localPos);
  }

  public static MovingVoiceSoundInstance create(AudioStream stream, Carriage carriage, BlockPos localPos) {
    return new MovingVoiceSoundInstanceImpl(stream, carriage, localPos);
  }

  @Override
  public CompletableFuture<AudioStream> getAudioStream(SoundBufferLibrary loader, ResourceLocation id, boolean repeatInstantly) {
    return CompletableFuture.completedFuture(stream);
  }
}
