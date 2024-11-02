package purplecreate.tramways.content.announcements.sound;

import javazoom.jl.decoder.*;
import net.minecraft.client.sounds.AudioStream;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import javax.sound.sampled.AudioFormat;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.Arrays;

// Stolen from https://github.com/NyakoFox/TikTokNarrator/blob/2403c4bf37eee4a35be121010a62d5c9ac269130/src/main/java/gay/nyako/tiktoknarrator/Mp3AudioStream.java
// (it's licensed with CC0 so i'm allowed to)
// Fixed a bit of the code with help from https://stackoverflow.com/questions/13959599/jlayer-mono-mp3-to-pcm-decoding
public class MP3AudioStream extends InputStream implements AudioStream {
  private final ByteBuffer buffer = ByteBuffer.allocate(Short.BYTES * Obuffer.OBUFFERSIZE).order(ByteOrder.LITTLE_ENDIAN);
  private final Decoder decoder = new Decoder();

  private final InputStream input;
  private final Bitstream bitstream;

  private SampleBuffer output;
  private AudioFormat format;

  int divider;

  public MP3AudioStream(InputStream input) throws IOException {
    this.input = input;
    this.bitstream = new Bitstream(input);
    if (this.readFrame()) {
      throw new IOException("Could not find header.");
    }
  }

  private boolean readFrame() throws IOException {
    this.buffer.clear();
    try {
      Header header = bitstream.readFrame();
      if (header == null) {
        // EOF
        this.buffer.flip();
        return true;
      }

      if (this.output == null) {
        int channels = header.mode() == Header.SINGLE_CHANNEL ? 1 : 2;
        this.output = new SampleBuffer(header.sample_frequency(), channels);
        this.decoder.setOutputBuffer(this.output);
        this.format = new AudioFormat(header.frequency(), 16, channels, true, false);

        this.divider = 1;
        if (header.frequency() < 44100) this.divider *= 2;
        if (channels == 1) this.divider *= 2;
      }

      if (decoder.decodeFrame(header, bitstream) != this.output) {
        throw new IllegalStateException("Output buffers are different.");
      }
    } catch (ArrayIndexOutOfBoundsException e) {
      // This is thrown when the end of the stream is reached.
    } catch (Exception e) {
      throw new IOException(e);
    }

    short[] pcm = this.output.getBuffer();
    for (int i = 0; i < pcm.length / divider; i++) {
      this.buffer.putShort(pcm[i]);
    }
    this.buffer.flip();

    this.bitstream.closeFrame();
    return false;
  }

  @Override
  public int read() throws IOException {
    if (!this.buffer.hasRemaining() && this.readFrame()) {
      return -1;
    }
    return ((int) this.buffer.get()) & 0xFF;
  }

  @Override
  public int read(byte @NotNull [] bytes, int offset, int length) throws IOException {
    int count = 0;

    while (count < length) {
      if (!this.buffer.hasRemaining() && this.readFrame()) {
        return count > 0 ? count : -1;
      }
      count += BufferUtils.read(this.buffer, bytes, offset + count, length, count);
    }

    return count;
  }

  @Override
  public int available() throws IOException {
    return this.input.available();
  }

  @Override
  public void close() throws IOException {
    try {
      this.bitstream.close();
    } catch (BitstreamException e) {
      throw new IOException(e);
    }
  }

  @Override
  public @NotNull AudioFormat getFormat() {
    return this.format;
  }

  @Override
  public ByteBuffer read(int size) throws IOException {
    byte[] data = new byte[size];
    int count = IOUtils.read(this, data);

    ByteBuffer dest = ByteBuffer.allocateDirect(count);
    dest.order(ByteOrder.nativeOrder());
    ByteBuffer src = ByteBuffer.wrap(size != count ? Arrays.copyOf(data, count) : data);
    src.order(ByteOrder.LITTLE_ENDIAN);

    ShortBuffer destShort = dest.asShortBuffer();
    ShortBuffer srcShort = src.asShortBuffer();
    while (srcShort.hasRemaining()) {
      destShort.put(srcShort.get());
    }

    dest.rewind();
    return dest;
  }

  static class BufferUtils {
    public static int read(ByteBuffer buffer, byte[] bytes, int offset, int length, int count) {
      length = Math.min(buffer.remaining(), length - count);
      buffer.get(bytes, offset, length);
      return length;
    }
  }
}