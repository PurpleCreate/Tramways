package purplecreate.tramways.content.announcements.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sounds.AudioStream;
import purplecreate.tramways.Tramways;
import purplecreate.tramways.config.MessageConfig;
import purplecreate.tramways.content.announcements.sound.MinimalSoundEngine;
import purplecreate.tramways.content.announcements.sound.MovingVoiceSoundInstance;
import com.simibubi.create.Create;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.Train;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import purplecreate.tramways.util.Env;
import purplecreate.tramways.util.S2CPacket;

import java.util.UUID;

public class PlayMovingVoiceS2CPacket implements S2CPacket {
  final String voice;
  final MessageConfig content;
  final BlockPos localPos;
  final UUID trainId;
  final int carriageId;

  public PlayMovingVoiceS2CPacket(String voice, MessageConfig content, BlockPos localPos, Carriage carriage) {
    this(voice, content, localPos, carriage.train.id, carriage.train.carriages.indexOf(carriage));
  }

  public PlayMovingVoiceS2CPacket(String voice, MessageConfig content, BlockPos localPos, UUID trainId, int carriageId) {
    this.voice = voice;
    this.content = content;
    this.localPos = localPos;
    this.trainId = trainId;
    this.carriageId = carriageId;
  }

  public static PlayMovingVoiceS2CPacket read(FriendlyByteBuf buffer) {
    String voice = buffer.readUtf();
    MessageConfig content = MessageConfig.readBytes(buffer);
    BlockPos localPos = buffer.readBlockPos();
    UUID trainId = buffer.readUUID();
    int carriageId = buffer.readVarInt();

    return new PlayMovingVoiceS2CPacket(voice, content, localPos, trainId, carriageId);
  }

  public void write(FriendlyByteBuf buffer) {
    buffer.writeUtf(voice);
    content.writeBytes(buffer);
    buffer.writeBlockPos(localPos);
    buffer.writeUUID(trainId);
    buffer.writeVarInt(carriageId);
  }

  @Environment(EnvType.CLIENT)
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

      AudioStream audioStream = PacketHandler.getAudioStream(voice, content);

      if (audioStream != null)
        MinimalSoundEngine.play(
          MovingVoiceSoundInstance.create(audioStream, carriage, localPos)
        );
    });
  }
}
