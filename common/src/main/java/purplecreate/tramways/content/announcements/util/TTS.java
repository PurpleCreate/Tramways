package purplecreate.tramways.content.announcements.util;

import purplecreate.tramways.Tramways;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.UUID;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

public class TTS {
  private static final String TOKEN = "6A5AA1D4EAFF4E9FB37E23D68491D6F4";
  private static final String SEC_VERSION = "1-130.0.2849.68";
  private static final byte[] PATH_AUDIO = new byte[]{0x50, 0x61, 0x74, 0x68, 0x3a, 0x61, 0x75, 0x64, 0x69, 0x6f, 0xd, 0xa};

  private final String requestId = UUID.randomUUID().toString().replace("-", "");
  private final String voice;
  private final String content;
  private final Consumer<byte[]> onData;
  private final CountDownLatch latch = new CountDownLatch(1);

  public TTS(String voice, String content, Consumer<byte[]> onData) {
    this.voice = voice;
    this.content = content;
    this.onData = onData;
  }

  public static InputStream stream(String voice, String content) {
    PipedInputStream in;
    PipedOutputStream out;

    try {
      in = new PipedInputStream();
      out = new PipedOutputStream(in);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    new Thread(() ->
      new TTS(
        voice,
        content,
        (bytes) -> {
          try {
            if (bytes == null) {
              out.close();
            } else {
              out.write(bytes);
            }
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        }
      ).start()
    ).start();

    return in;
  }

  private URI generateURI() {
    long time = System.currentTimeMillis();
    long ticks = (long) (Math.floor((time / 1000.0) + 11644473600L) * 10000000);
    long roundedTicks = ticks - (ticks % 3000000000L);
    String plaintext = roundedTicks + TOKEN;
    String sec = HexFormat.of().withUpperCase().formatHex(
      Hash
        .getSha256()
        .digest(plaintext.getBytes(StandardCharsets.UTF_8))
    );

    return URI.create(
      String.format(
        "wss://speech.platform.bing.com/consumer/speech/synthesize/readaloud/edge/v1?trustedclienttoken=%s&Sec-MS-GEC=%s&Sec-MS-GEC-Version=%s",
        TOKEN,
        sec,
        SEC_VERSION
      )
    );
  }

  private String generateHello() {
    return """
      Content-Type:application/json; charset=utf-8\r
      Path:speech.config\r
      \r
      {
        "context": {
          "synthesis": {
            "audio": {
              "metadataoptions": {
                "sentenceBoundaryEnabled": "false",
                 "wordBoundaryEnabled": "false"
              },
              "outputFormat": "audio-24khz-48kbitrate-mono-mp3"
            }
          }
        }
      }""";
  }

  private String generateRequest() {
    return """
      X-RequestId:%s\r
      Content-Type:application/ssml+xml\r
      Path:ssml\r
      \r
      <speak version="1.0" xmlns="http://www.w3.org/2001/10/synthesis" xml:lang="%s">
        <voice name="%s">
          <prosody pitch="+0Hz" rate="+0%%" volume="+0%%">
            %s
          </prosody>
        </voice>
      </speak>""".formatted(requestId, voice.substring(0, 5), voice, content);
  }

  public void start() {
    WebSocket ws;

    try {
      ws = HttpClient
        .newHttpClient()
        .newWebSocketBuilder()
        .header("Origin", "chrome-extension://jdiccldimpdaibmpdkjnbmckianbfold")
        .header("Pragma", "no-cache")
        .header("Cache-Control", "no-cache")
        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.74 Safari/537.36 Edg/99.0.1150.55")
        .buildAsync(generateURI(), new TTSListener())
        .join();
    } catch (CompletionException e) {
      onData.accept(null);
      return;
    }

    ws.sendText(generateHello(), true);
    ws.sendText(generateRequest(), true);

    try {
      latch.await();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  private class TTSListener implements WebSocket.Listener {
    @Override
    public CompletionStage<?> onBinary(WebSocket webSocket, ByteBuffer data, boolean last) {
      byte[] message = new byte[data.remaining()];
      int start = -1;

      data.get(message);

      for (int i = 0; i < message.length - PATH_AUDIO.length; i++) {
        boolean found = true;

        for (int j = 0; j < PATH_AUDIO.length; j++) {
          if (message[i + j] != PATH_AUDIO[j]) {
            found = false;
            break;
          }
        }

        if (found) {
          start = i + PATH_AUDIO.length;
          break;
        }
      }

      if (start > -1) {
        onData.accept(Arrays.copyOfRange(message, start, message.length));
      }

      return WebSocket.Listener.super.onBinary(webSocket, data, last);
    }

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
      String message = data.toString();

      if (message.contains("Path:turn.end")) {
        webSocket.sendClose(1000, "");
        onData.accept(null);
        latch.countDown();
      }

      return WebSocket.Listener.super.onText(webSocket, data, last);
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
      Tramways.LOGGER.error(error.toString());
    }
  }
}