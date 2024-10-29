package purplecreate.tramways.content.announcements.util;

import purplecreate.tramways.services.FFMPEG;
import purplecreate.tramways.services.TTS;
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

  public InputStream getFile(String voice, String content) {
    InputStream stream = tryGetFileFromCache(voice, content);

    if (stream == null)
      stream = tryGetFileFromApi(voice, content);

    return stream;
  }

  public float getFileDuration(String voice, String content) {
    return FFMPEG.getDuration(new File(folder, getFileName(voice, content)).toString());
  }

  private MessageDigest getDigest() {
    try {
      return MessageDigest.getInstance("SHA-256");
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }

  private String getFileName(String voice, String content) {
    String contentHash = HexFormat.of().formatHex(getDigest().digest(content.getBytes(StandardCharsets.UTF_8)));
    return voice + "-" + contentHash + ".ogg";
  }

  private InputStream tryGetFileFromCache(String voice, String content) {
    try {
      return new FileInputStream(new File(folder, getFileName(voice, content)));
    } catch (FileNotFoundException e) {
      return null;
    }
  }

  private InputStream tryGetFileFromApi(String voice, String content) {
    FileOutputStream file;

    try {
      file = new FileOutputStream(new File(folder, getFileName(voice, content)));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    Process ff = FFMPEG.convertToOGG();
    OutputStream ffstdin = ff.getOutputStream();
    InputStream ffstdout = ff.getInputStream();

    new Thread(() ->
      new TTS(voice, content, (data) -> {
        try {
          if (data == null) {
            ffstdin.close();
          } else {
            ffstdin.write(data);
          }
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }).start()
    ).start();

    return new InputStream() {
      @Override
      public int read() throws IOException {
        int b = ffstdout.read();

        if (b == -1) {
          file.close();
        } else {
          file.write(b);
        }

        return b;
      }
    };
  }
}
