package purplecreate.tramways.services;

import java.io.IOException;
import java.io.InputStream;

public class FFMPEG {
  public static boolean exists() {
    try {
      for (String command : new String[] {"ffmpeg", "ffprobe"}) {
        new ProcessBuilder(command, "-version")
          .redirectErrorStream(true)
          .start()
          .waitFor();
      }

      return true;
    } catch (IOException | InterruptedException e) {
      return false;
    }
  }

  public static Process convertToOGG() {
    try {
      return new ProcessBuilder(
        "ffmpeg", "-i", "pipe:0", "-c:a", "libvorbis", "-f", "ogg", "pipe:1"
      ).start();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static float getDuration(String path) {
    try {
      Process process = new ProcessBuilder(
        "ffprobe", "-of", "default=noprint_wrappers=1:nokey=1", "-show_entries", "format=duration", path
      ).start();
      InputStream ffstdout = process.getInputStream();

      if (process.waitFor() != 0)
        return -1F;

      byte[] _result = ffstdout.readAllBytes();
      String result = new String(_result);

      return Float.parseFloat(result.trim());
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
