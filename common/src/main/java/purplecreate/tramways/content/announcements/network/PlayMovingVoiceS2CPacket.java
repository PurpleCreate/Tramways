package purplecreate.tramways.content.announcements.network;

import purplecreate.tramways.Tramways;
import purplecreate.tramways.content.announcements.sound.MovingVoiceSoundInstance;
import com.simibubi.create.Create;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.Train;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import purplecreate.tramways.content.announcements.util.TTSFileManager;
import purplecreate.tramways.util.Env;
import purplecreate.tramways.util.S2CPacket;

import java.io.InputStream;
import java.util.UUID;

public class PlayMovingVoiceS2CPacket implements S2CPacket {
  final String voice;
  final String content;
  final BlockPos localPos;
  final UUID trainId;
  final int carriageId;

  public PlayMovingVoiceS2CPacket(String voice, String content, BlockPos localPos, Carriage carriage) {
    this(voice, content, localPos, carriage.train.id, carriage.train.carriages.indexOf(carriage));
  }

  public PlayMovingVoiceS2CPacket(String voice, String content, BlockPos localPos, UUID trainId, int carriageId) {
    this.voice = voice;
    this.content = content;
    this.localPos = localPos;
    this.trainId = trainId;
    this.carriageId = carriageId;
  }

  public static PlayMovingVoiceS2CPacket read(FriendlyByteBuf buffer) {
    String voice = buffer.readUtf();
    String content = buffer.readUtf();
    BlockPos localPos = buffer.readBlockPos();
    UUID trainId = buffer.readUUID();
    int carriageId = buffer.readVarInt();

    return new PlayMovingVoiceS2CPacket(voice, content, localPos, trainId, carriageId);
  }

  public void write(FriendlyByteBuf buffer) {
    buffer.writeUtf(voice);
    buffer.writeUtf(content);
    buffer.writeBlockPos(localPos);
    buffer.writeUUID(trainId);
    buffer.writeVarInt(carriageId);
  }

  public void handle(Minecraft mc) {
    Env.unsafeRunWhenOn(Env.CLIENT, () -> () -> {
      Level level = mc.level;

      Train train = Create.RAILWAYS.sided(level).trains.get(trainId);
      if (train == null) {
        Tramways.LOGGER.warn("Couldn't play voice: Requested train is null");
        return;
      }

      Carriage carriage = train.carriages.get(carriageId);
      if (carriage == null) {
        Tramways.LOGGER.warn("Couldn't play voice: Requested carriage is null");
        return;
      }

      InputStream stream = TTSFileManager.instance.cachedStream(voice, content);
      mc.getSoundManager().play(
        MovingVoiceSoundInstance.create(stream, carriage, localPos)
      );
    });
  }
}
