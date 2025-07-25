package purplecreate.tramways.content.announcements.sound.fabric;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.sound.v1.FabricSoundInstance;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.client.sounds.SoundBufferLibrary;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import purplecreate.tramways.content.announcements.sound.VoiceSoundInstance;

import java.util.concurrent.CompletableFuture;

@Environment(EnvType.CLIENT)
public class VoiceSoundInstanceImpl extends VoiceSoundInstance implements FabricSoundInstance {
  protected VoiceSoundInstanceImpl(AudioStream stream, BlockPos pos) {
    super(stream, pos);
  }

  public static VoiceSoundInstance create(AudioStream stream, BlockPos pos) {
    return new VoiceSoundInstanceImpl(stream, pos);
  }

  @Override
  public CompletableFuture<AudioStream> getAudioStream(SoundBufferLibrary loader, ResourceLocation id, boolean repeatInstantly) {
    return CompletableFuture.completedFuture(stream);
  }
}
