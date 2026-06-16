package purplecreate.tramways.content.announcements.engine;

import com.mojang.blaze3d.audio.Channel;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.Train;
import de.mrjulsen.crn.data.train.portable.StationDisplayData;
import de.mrjulsen.crn.data.train.portable.TrainDisplayData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import org.jetbrains.annotations.Nullable;
import purplecreate.tramways.TBlocks;
import purplecreate.tramways.Tramways;
import purplecreate.tramways.content.announcements.config.AnnouncementVariant;
import purplecreate.tramways.mixins.AbstractContraptionEntityAccessor;
import purplecreate.tramways.mixins.ChannelAccessor;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Environment(EnvType.CLIENT)
public class SoundEngine {
  private static final ExecutorService THREAD = Executors.newSingleThreadExecutor(t ->
    new Thread(t, "Tramways Sound Engine")
  );

  private static Channel PREVIEW = null;
  private static final Map<BlockPos, List<AudioStream>> STATION_QUEUE = new HashMap<>();
  private static final Map<BlockPos, ChannelHolder> STATION_CHANNELS = new HashMap<>();
  private static final Map<Train, List<AudioStream>> TRAIN_QUEUE = new HashMap<>();
  private static final Map<Train, List<TrainChannelHolder>> TRAIN_CHANNELS = new HashMap<>();

  private static float getSourceVolume(SoundSource category) {
    return category != null && category != SoundSource.MASTER
      ? Minecraft.getInstance().options.getSoundSourceVolume(category)
      : 1.0F;
  }

  private static void setPosition(Channel channel, @Nullable CarriageContraptionEntity cce, BlockPos pos) {
    if (cce != null) {
      StructureTransform t = ((AbstractContraptionEntityAccessor)cce).tramways$makeStructureTransform();
      pos = t.apply(pos);
    }

    channel.setSelfPosition(pos.getCenter());
  }

  private static Channel createChannel(AudioStream stream, boolean train, boolean preview) {
    Channel channel = ChannelAccessor.createChannel();
    channel.setPitch(1);
    channel.setVolume(getSourceVolume(SoundSource.BLOCKS));
    channel.linearAttenuation(train ? 16 : 64);
    channel.setLooping(false);
    channel.setRelative(preview);
    channel.attachBufferStream(stream);
    return channel;
  }

  public static void playPreview(AudioStream stream) {
    stopPreview();
    PREVIEW = createChannel(stream, false, true);
    PREVIEW.play();
  }

  public static void stopPreview() {
    if (PREVIEW != null) {
      PREVIEW.destroy();
    }
  }

  public static boolean isPreviewStopped() {
    return PREVIEW == null || PREVIEW.stopped();
  }

  public static void enqueue(BlockPos speakerPos, AnnouncementVariant variant, StationDisplayData data) {
    THREAD.submit(() -> {
      try {
        List<AudioStream> queue = STATION_QUEUE.computeIfAbsent(speakerPos, $ -> new ArrayList<>());
        queue.addAll(variant.play(null, data));
        queue.add(null);
      } catch (Throwable e) {
        Tramways.LOGGER.warn("An exception occurred whilst loading an announcement", e);
      }
    });
  }

  public static void enqueue(Train train, AnnouncementVariant variant, TrainDisplayData data) {
    THREAD.submit(() -> {
      try {
        List<AudioStream> queue = TRAIN_QUEUE.computeIfAbsent(train, $ -> new ArrayList<>());
        queue.addAll(variant.play(data, null));
        queue.add(null);
      } catch (Throwable e) {
        Tramways.LOGGER.warn("An exception occurred whilst loading an announcement", e);
      }
    });
  }

  public static void tick() {
    THREAD.submit(() -> {
      if (PREVIEW != null) {
        PREVIEW.updateStream();

        if (PREVIEW.stopped()) {
          PREVIEW.destroy();
          PREVIEW = null;
        }
      }

      STATION_QUEUE.forEach((speakerPos, queue) -> {
        if (STATION_CHANNELS.containsKey(speakerPos) || queue.isEmpty()) return;

        AudioStream stream = queue.remove(0);
        Channel channel = createChannel(stream, false, false);
        setPosition(channel, null, speakerPos);
        channel.play();

        STATION_CHANNELS.put(speakerPos, new ChannelHolder(channel));
      });

      for (Iterator<Map.Entry<BlockPos, ChannelHolder>> it = STATION_CHANNELS.entrySet().iterator(); it.hasNext(); ) {
        Map.Entry<BlockPos, ChannelHolder> entry = it.next();
        ChannelHolder h = entry.getValue();

        h.channel.updateStream();

        if (h.channel.stopped()) {
          List<AudioStream> queue = STATION_QUEUE.get(entry.getKey());

          int minTicksToDestroy = queue.isEmpty() || queue.get(0) == null
            ? 20 * 4
            : 0;

          h.ticksStopped++;
          if (h.ticksStopped >= minTicksToDestroy) {
            h.channel.destroy();
            it.remove();
          }
          continue;
        }

        h.channel.setVolume(getSourceVolume(SoundSource.BLOCKS));
      }

      TRAIN_QUEUE.forEach((train, queue) -> {
        if (TRAIN_CHANNELS.containsKey(train) || queue.isEmpty()) return;

        AudioStream stream = queue.remove(0);
        List<TrainPos> speakers = new ArrayList<>();

        for (Carriage carriage : train.carriages) {
          carriage.forEachPresentEntity(cce ->
            cce.getContraption().getBlocks().forEach((pos, block) -> {
              if (!block.state().is(TBlocks.SPEAKER.get())) return;
              speakers.add(new TrainPos(cce, pos));
            })
          );
        }

        if (speakers.isEmpty()) return;

        AudioStreamMultiplexer multiplexer = new AudioStreamMultiplexer(stream, speakers.size());
        List<TrainChannelHolder> channels = new ArrayList<>();

        int i = 0;
        for (TrainPos speaker : speakers) {
          Channel channel = createChannel(multiplexer.get(i++), true, false);
          setPosition(channel, speaker.cce, speaker.pos);
          channel.play();

          channels.add(new TrainChannelHolder(channel, speaker));
        }

        TRAIN_CHANNELS.put(train, channels);
      });

      for (Iterator<Map.Entry<Train, List<TrainChannelHolder>>> it = TRAIN_CHANNELS.entrySet().iterator(); it.hasNext(); ) {
        Map.Entry<Train, List<TrainChannelHolder>> entry = it.next();

        for (ListIterator<TrainChannelHolder> it1 = entry.getValue().listIterator(); it.hasNext(); ) {
          TrainChannelHolder h = it1.next();

          h.channel.updateStream();

          if (h.channel.stopped()) {
            List<AudioStream> queue = TRAIN_QUEUE.get(entry.getKey());

            int minTicksToDestroy = queue.isEmpty() || queue.get(0) == null
              ? 20 * 4
              : 0;

            h.ticksStopped++;
            if (h.ticksStopped >= minTicksToDestroy) {
              h.channel.destroy();
              it1.remove();
            }
            continue;
          }

          setPosition(h.channel, h.pos.cce, h.pos.pos);
          h.channel.setVolume(getSourceVolume(SoundSource.BLOCKS));
        }

        if (entry.getValue().isEmpty()) {
          it.remove();
        }
      }
    });
  }

  public static void pauseAll() {
    for (ChannelHolder h : STATION_CHANNELS.values())
      h.channel.pause();

    for (List<TrainChannelHolder> channels : TRAIN_CHANNELS.values())
      for (TrainChannelHolder h : channels)
        h.channel.pause();
  }

  public static void resumeAll() {
    for (ChannelHolder h : STATION_CHANNELS.values())
      h.channel.unpause();

    for (List<TrainChannelHolder> channels : TRAIN_CHANNELS.values())
      for (TrainChannelHolder h : channels)
        h.channel.unpause();
  }

  public static void clean() {
    for (ChannelHolder h : STATION_CHANNELS.values())
      h.channel.destroy();

    for (List<TrainChannelHolder> channels : TRAIN_CHANNELS.values())
      for (TrainChannelHolder h : channels)
        h.channel.destroy();
  }

  private record TrainPos(
    CarriageContraptionEntity cce,
    BlockPos pos
  ) {}

  private static class ChannelHolder {
    public final Channel channel;
    public int ticksStopped = 0;

    public ChannelHolder(Channel channel) {
      this.channel = channel;
    }
  }

  private static class TrainChannelHolder extends ChannelHolder {
    public final TrainPos pos;

    public TrainChannelHolder(Channel channel, TrainPos pos) {
      super(channel);
      this.pos = pos;
    }
  }
}
