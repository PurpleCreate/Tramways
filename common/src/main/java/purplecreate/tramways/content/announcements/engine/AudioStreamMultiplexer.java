package purplecreate.tramways.content.announcements.engine;

import net.minecraft.client.sounds.AudioStream;

import javax.sound.sampled.AudioFormat;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class AudioStreamMultiplexer {
  private final AudioStream source;
  private final MultiplexAudioStream[] channels;
  private final List<ByteBuffer> buf = new ArrayList<>();
  private int closedChannels = 0;

  public AudioStreamMultiplexer(AudioStream source, int channels) {
    this.source = source;
    this.channels = new MultiplexAudioStream[channels];
  }

  public AudioStream get(int channel) {
    if (channels[channel] == null) {
      channels[channel] = new MultiplexAudioStream();
    }
    return channels[channel];
  }

  private class MultiplexAudioStream implements AudioStream {
    private int current = 0;
    private boolean closed = false;

    @Override
    public AudioFormat getFormat() {
      return source.getFormat();
    }

    @Override
    public ByteBuffer read(int size) throws IOException {
      if (current >= buf.size()) {
        ByteBuffer b = source.read(size);
        buf.add(b);
      }

      return buf.get(current++).slice();
    }

    @Override
    public void close() throws IOException {
      if (closed) return;

      closed = true;
      closedChannels++;

      if (closedChannels >= channels.length) {
        source.close();
      }
    }
  }
}
