package purplecreate.tramways.content.announcements.sound;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

@Environment(EnvType.CLIENT)
public class VoiceSoundInstance extends AbstractSoundInstance {
  protected final AudioStream stream;

  protected VoiceSoundInstance(AudioStream stream, BlockPos pos) {
    super(
      ResourceLocation.fromNamespaceAndPath("minecraft", "ambient.cave"),
      SoundSource.BLOCKS,
      RandomSource.create()
    );

    this.stream = stream;
    this.x = pos.getX();
    this.y = pos.getY();
    this.z = pos.getZ();
  }

  @ExpectPlatform
  public static VoiceSoundInstance create(AudioStream stream, BlockPos pos) {
    throw new AssertionError();
  }

  @Override
  public Sound getSound() {
    return new Sound(sound.getPath(), sound.getVolume(), sound.getPitch(), sound.getWeight(), Sound.Type.FILE, true, false, 16);
  }
}
