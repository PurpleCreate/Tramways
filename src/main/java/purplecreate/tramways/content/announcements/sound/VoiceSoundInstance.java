package purplecreate.tramways.content.announcements.sound;

import com.mojang.blaze3d.audio.OggAudioStream;
import net.minecraft.Util;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.client.sounds.SoundBufferLibrary;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

public class VoiceSoundInstance extends AbstractSoundInstance {
  private final InputStream stream;

  public VoiceSoundInstance(InputStream stream, BlockPos pos) {
    super(
      new ResourceLocation("minecraft", "ambient.cave"),
      SoundSource.BLOCKS,
      RandomSource.create()
    );

    this.stream = stream;
    this.x = pos.getX();
    this.y = pos.getY();
    this.z = pos.getZ();
  }

  @Override
  public CompletableFuture<AudioStream> getStream(SoundBufferLibrary soundBuffers, Sound sound, boolean looping) {
    return CompletableFuture.supplyAsync(() -> {
      try {
        return new OggAudioStream(stream);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }, Util.backgroundExecutor());
  }

  @Override
  public Sound getSound() {
    return new Sound(sound.getPath().getPath(), sound.getVolume(), sound.getPitch(), sound.getWeight(), Sound.Type.FILE, true, false, 16);
  }
}
