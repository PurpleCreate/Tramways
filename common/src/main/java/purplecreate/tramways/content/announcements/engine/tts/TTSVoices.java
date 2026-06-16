package purplecreate.tramways.content.announcements.engine.tts;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import purplecreate.tramways.Tramways;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TTSVoices {
  private static final Gson GSON = new Gson();
  public static final Voice DEFAULT_VOICE;
  public static final List<Voice> VALUES;

  static {
    Optional<Voice> defaultVoice = Optional.empty();
    List<Voice> voices = new ArrayList<>();
    HttpResponse<String> res;

    try {
      res = HttpClient
        .newHttpClient()
        .send(
          HttpRequest
            .newBuilder()
            .uri(URI.create("https://speech.platform.bing.com/consumer/speech/synthesize/readaloud/voices/list?trustedclienttoken=" + TTSStream.TOKEN))
            .build(),
          HttpResponse.BodyHandlers.ofString()
        );

      for (JsonElement elem : GSON.fromJson(res.body(), JsonArray.class)) {
        JsonObject obj = elem.getAsJsonObject();
        Voice voice = new Voice(obj.get("ShortName").getAsString(), obj.get("FriendlyName").getAsString());
        voices.add(voice);

        if (voice.id().equals("en-GB-SoniaNeural")) {
          defaultVoice = Optional.of(voice);
        }
      }
    } catch (Throwable e) {
      Tramways.LOGGER.warn("An error occurred whilst fetching announcement voices", e);
    }

    DEFAULT_VOICE = defaultVoice.orElseThrow();
    VALUES = voices;
  }

  public static Optional<Voice> fromId(String id) {
    for (Voice voice : VALUES) {
      if (voice.id.equals(id)) {
        return Optional.of(voice);
      }
    }

    return Optional.empty();
  }

  public static void init() {
  }

  public record Voice(String id, String longName) {
    public String name() {
      return longName.replaceAll("(Microsoft |Multilingual| Online \\(Natural\\)| \\(Preview\\))", "");
    }
  }
}
