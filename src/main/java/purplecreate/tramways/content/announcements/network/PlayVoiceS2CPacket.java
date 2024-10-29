package purplecreate.tramways.content.announcements.network;

import purplecreate.tramways.content.announcements.sound.VoiceSoundInstance;
import purplecreate.tramways.content.announcements.util.TTSFileManager;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import purplecreate.tramways.util.QueuedPacket;

import java.io.InputStream;
import java.util.function.Supplier;

public class PlayVoiceS2CPacket extends QueuedPacket {
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

  public void handle(Supplier<NetworkEvent.Context> context) {
    context.get().enqueueWork(() ->
      DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
        addToQueue(pos.toString())
      )
    );

    context.get().setPacketHandled(true);
  }

  protected long handleQueued() {
    InputStream stream = TTSFileManager.instance.getFile(voice, content);
    Minecraft.getInstance().getSoundManager().play(
      new VoiceSoundInstance(stream, pos)
    );

    float duration = TTSFileManager.instance.getFileDuration(voice, content);
    return (long)Math.ceil(duration < 0 ? 10 : duration);
  }
}
