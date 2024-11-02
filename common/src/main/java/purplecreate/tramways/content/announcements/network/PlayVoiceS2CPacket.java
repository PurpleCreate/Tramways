package purplecreate.tramways.content.announcements.network;

import purplecreate.tramways.content.announcements.sound.VoiceSoundInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import purplecreate.tramways.content.announcements.util.TTSFileManager;
import purplecreate.tramways.util.Env;
import purplecreate.tramways.util.S2CPacket;

import java.io.InputStream;

public class PlayVoiceS2CPacket implements S2CPacket {
  final String voice;
  final String content;
  final BlockPos pos;

  public PlayVoiceS2CPacket(String voice, String content, BlockPos pos) {
    this.voice = voice;
    this.content = content;
    this.pos = pos;
  }

  public static PlayVoiceS2CPacket read(FriendlyByteBuf buffer) {
    String voice = buffer.readUtf();
    String content = buffer.readUtf();
    BlockPos pos = buffer.readBlockPos();
    return new PlayVoiceS2CPacket(voice, content, pos);
  }

  public void write(FriendlyByteBuf buffer) {
    buffer.writeUtf(voice);
    buffer.writeUtf(content);
    buffer.writeBlockPos(pos);
  }

  public void handle(Minecraft mc) {
    Env.unsafeRunWhenOn(Env.CLIENT, () -> () -> {
      InputStream stream = TTSFileManager.instance.cachedStream(voice, content);
      Minecraft.getInstance().getSoundManager().play(
        VoiceSoundInstance.create(stream, pos)
      );
    });
  }
}
