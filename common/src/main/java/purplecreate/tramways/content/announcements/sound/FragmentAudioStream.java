package purplecreate.tramways.content.announcements.sound;

import net.minecraft.client.sounds.AudioStream;

import javax.sound.sampled.AudioFormat;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

public class FragmentAudioStream implements AudioStream {
  List<AudioStream> streams;
  int current = 0;

  public FragmentAudioStream(List<AudioStream> streams) {
    this.streams = streams;
  }

  @Override
  public AudioFormat getFormat() {
    if (current >= streams.size())
      return streams.get(streams.size() - 1).getFormat();
    return streams.get(current).getFormat();
  }

  @Override
  public ByteBuffer read(int size) throws IOException {
    if (current >= streams.size())
      return ByteBuffer.allocate(0);

    ByteBuffer buf = streams.get(current).read(size);

    if (!buf.hasRemaining()) {
      streams.get(current).close();
      current++;
      if (current < streams.size())
        buf = streams.get(current).read(size);
    }

    return buf;
  }

  @Override
  public void close() throws IOException {
    for (AudioStream stream : streams)
      stream.close();
  }
}
