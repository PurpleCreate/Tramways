package purplecreate.tramways.content.announcements.sound;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.Util;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

public class VoiceSoundInstance extends AbstractSoundInstance {
  protected final InputStream stream;

  protected VoiceSoundInstance(InputStream stream, BlockPos pos) {
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

  @ExpectPlatform
  public static VoiceSoundInstance create(InputStream stream, BlockPos pos) {
    throw new AssertionError();
  }

  protected CompletableFuture<AudioStream> getStreamInternal(InputStream stream) {
    return CompletableFuture.supplyAsync(() -> {
      try {
        return new MP3AudioStream(stream);
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
