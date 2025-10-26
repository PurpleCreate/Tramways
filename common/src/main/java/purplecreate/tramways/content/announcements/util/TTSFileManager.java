package purplecreate.tramways.content.announcements.util;

import net.minecraft.client.Minecraft;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public class TTSFileManager {
  public static final TTSFileManager instance = new TTSFileManager();

  private final File folder = new File(
    Minecraft.getInstance().gameDirectory,
    "tramways_voice"
  );

  private TTSFileManager() {
    folder.mkdirs();
  }

  public InputStream cachedStream(String voice, String content) {
    File file = getFile(voice, content);

    if (file.exists()) {
      try {
        return new FileInputStream(file);
      } catch (FileNotFoundException e) {
        throw new RuntimeException(e);
      }
    }

    OutputStream fileOutput;
    try {
      fileOutput = new FileOutputStream(file);
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }

    InputStream stream = TTS.stream(voice, content);

    return new InputStream() {
      @Override
      public int read() throws IOException {
        int i = stream.read();

        if (i == -1) {
          fileOutput.close();
        } else {
          fileOutput.write(i);
        }

        return i;
      }

      @Override
      public void close() throws IOException {
        stream.close();
        fileOutput.close();
      }
    };
  }

  private String getFileName(String voice, String content) {
    String contentHash = HexFormat.of().formatHex(Hash.getSha256().digest(content.getBytes(StandardCharsets.UTF_8)));
    return voice + "-" + contentHash + ".mp3";
  }

  public File getFile(String voice, String content) {
    return new File(folder, getFileName(voice, content));
  }
}
