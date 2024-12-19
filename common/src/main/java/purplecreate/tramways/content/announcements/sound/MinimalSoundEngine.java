package purplecreate.tramways.content.announcements.sound;

import com.mojang.blaze3d.audio.Channel;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import purplecreate.tramways.mixins.ChannelMixin;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CompletableFuture;

public class MinimalSoundEngine {
  private static List<Instance> records = new ArrayList<>();

  @ExpectPlatform
  private static CompletableFuture<AudioStream> getStream(SoundInstance instance) {
    throw new AssertionError();
  }

  private static float getSourceVolume(SoundSource category) {
    return category != null && category != SoundSource.MASTER
      ? Minecraft.getInstance().options.getSoundSourceVolume(category)
      : 1.0F;
  }

  private static void setPosition(SoundInstance instance, Channel channel) {
    Vec3 pos = new Vec3(instance.getX(), instance.getY(), instance.getZ());
    channel.setSelfPosition(pos);
  }

  public static void play(SoundInstance instance) {
    instance.resolve(Minecraft.getInstance().getSoundManager());

    Channel channel = ChannelMixin.createChannel();
    Sound sound = instance.getSound();

    float volumeMultiplier = instance.getVolume();
    float linearAttenuation = Math.max(volumeMultiplier, 1.0F) * sound.getAttenuationDistance();
    float pitch = Mth.clamp(instance.getPitch(), 0.5F, 2.0F);
    float volume = Mth.clamp(volumeMultiplier * getSourceVolume(instance.getSource()), 0.0F, 1.0F);

    channel.setPitch(pitch);
    channel.setVolume(volume);
    channel.linearAttenuation(linearAttenuation);
    channel.setLooping(false);
    setPosition(instance, channel);
    channel.setRelative(instance.isRelative());

    getStream(instance).thenAccept((stream) -> {
      channel.attachBufferStream(stream);
      channel.play();
    });

    records.add(new Instance(instance, channel));
  }

  public static void tick() {
    ListIterator<Instance> iter = records.listIterator();

    while (iter.hasNext()) {
      Instance rec = iter.next();
      SoundInstance instance = rec.instance;
      Channel channel = rec.channel;

      channel.updateStream();

      if (channel.stopped()) {
        iter.remove();
        continue;
      }

      if (instance instanceof TickableSoundInstance tickable) {
        if (tickable.isStopped()) {
          iter.remove();
          channel.stop();
          continue;
        }

        tickable.tick();
        setPosition(instance, channel);
      }
    }
  }

  public static void pauseAll() {
    for (Instance rec : records)
      rec.channel.pause();
  }

  public static void resumeAll() {
    for (Instance rec : records)
      rec.channel.unpause();
  }

  public static void stopAll() {
    for (Instance rec : records)
      rec.channel.stop();
  }

  record Instance(SoundInstance instance, Channel channel) {}
}
