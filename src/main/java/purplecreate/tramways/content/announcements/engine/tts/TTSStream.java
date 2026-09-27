package purplecreate.tramways.content.announcements.engine.tts;

import purplecreate.tramways.Tramways;
import purplecreate.tramways.content.announcements.engine.files.FileInfo.Hash;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

public class TTSStream extends PipedInputStream {
  static final String TOKEN = "6A5AA1D4EAFF4E9FB37E23D68491D6F4";
  private static final String CHROMIUM_VERSION = "143.0.3650.75";
  private static final String SEC_VERSION = "1-" + CHROMIUM_VERSION;
  private static final String PATH_AUDIO = "Path:audio\r\n";

  private final WebSocket ws;
  private final String requestId = UUID.randomUUID().toString().replace("-", "");
  private final PipedOutputStream out = new PipedOutputStream();

  public TTSStream(TTSVoices.Voice voice, String content) throws IOException {
    super();
    connect(out);

    Tramways.LOGGER.debug("Created TTS Stream with ID {} for announcement \"{}\"", requestId, content);

    ws = HttpClient
      .newHttpClient()
      .newWebSocketBuilder()
      .header("Origin", "chrome-extension://jdiccldimpdaibmpdkjnbmckianbfold")
      .header("Pragma", "no-cache")
      .header("Cache-Control", "no-cache")
      .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/%s Safari/537.36 Edg/%1$s".formatted(CHROMIUM_VERSION))
      .buildAsync(generateURI(), new TTSListener())
      .join();

    Tramways.LOGGER.debug("Connected to TTS API");

    ws.sendText(generateHello(), true);
    ws.sendText(generateRequest(voice, content), true);
  }

  private URI generateURI() {
    long epoch = -11644473600000L;
    long time = System.currentTimeMillis();
    long ticks = (time - epoch) * 10000;
    long roundedTicks = ticks - (ticks % 3000000000L);
    String plaintext = roundedTicks + TOKEN;
    String sec = HexFormat.of().withUpperCase().formatHex(
      Hash.SHA256.get().digest(plaintext.getBytes(StandardCharsets.UTF_8))
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

  private String generateRequest(TTSVoices.Voice voice, String content) {
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
      </speak>""".formatted(requestId, voice.id().substring(0, 5), voice.id(), content);
  }

  @Override
  public void close() throws IOException {
    super.close();
    ws.sendClose(WebSocket.NORMAL_CLOSURE, "");
  }

  private class TTSListener implements WebSocket.Listener {
    @Override
    public CompletionStage<?> onBinary(WebSocket webSocket, ByteBuffer data, boolean last) {
      byte[] message = new byte[data.remaining()];
      int start = -1;

      data.get(message);

      for (int i = 0; i < message.length - PATH_AUDIO.length(); i++) {
        boolean found = true;

        for (int j = 0; j < PATH_AUDIO.length(); j++) {
          if (message[i + j] != PATH_AUDIO.charAt(j)) {
            found = false;
            break;
          }
        }

        if (found) {
          start = i + PATH_AUDIO.length();
          break;
        }
      }

      if (start > -1) {
        try {
          out.write(message, start, message.length - start);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }

      return WebSocket.Listener.super.onBinary(webSocket, data, last);
    }

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
      String message = data.toString();

      if (message.contains("Path:turn.end")) {
        Tramways.LOGGER.debug("End of TTS Stream with ID {}", requestId);

        try {
          out.close();
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }

      return WebSocket.Listener.super.onText(webSocket, data, last);
    }

    @Override
    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
      Tramways.LOGGER.debug("Disconnected from TTS API ({}, {})", statusCode, reason);
      return WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
      Tramways.LOGGER.warn("An error occurred in the TTS Websocket connection", error);
    }
  }
}