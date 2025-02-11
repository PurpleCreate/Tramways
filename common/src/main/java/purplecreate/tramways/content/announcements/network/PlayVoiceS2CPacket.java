package purplecreate.tramways.content.announcements.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sounds.AudioStream;
import purplecreate.tramways.config.MessageConfig;
import purplecreate.tramways.content.announcements.sound.MinimalSoundEngine;
import purplecreate.tramways.content.announcements.sound.VoiceSoundInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import purplecreate.tramways.util.Env;
import purplecreate.tramways.util.S2CPacket;

public class PlayVoiceS2CPacket implements S2CPacket {
  final String voice;
  final MessageConfig content;
  final BlockPos pos;

  public PlayVoiceS2CPacket(String voice, MessageConfig content, BlockPos pos) {
    this.voice = voice;
    this.content = content;
    this.pos = pos;
  }

  public static PlayVoiceS2CPacket read(FriendlyByteBuf buffer) {
    String voice = buffer.readUtf();
    MessageConfig content = MessageConfig.readBytes(buffer);
    BlockPos pos = buffer.readBlockPos();
    return new PlayVoiceS2CPacket(voice, content, pos);
  }

  public void write(FriendlyByteBuf buffer) {
    buffer.writeUtf(voice);
    content.writeBytes(buffer);
    buffer.writeBlockPos(pos);
  }

  @Environment(EnvType.CLIENT)
  public void handle(Minecraft mc) {
    AudioStream audioStream = PacketHandler.getAudioStream(voice, content);

    if (audioStream != null)
      MinimalSoundEngine.play(
        VoiceSoundInstance.create(audioStream, pos)
      );
  }
}
