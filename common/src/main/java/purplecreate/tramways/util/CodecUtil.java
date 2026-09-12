package purplecreate.tramways.util;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import purplecreate.tramways.Tramways;

public class CodecUtil {
  public static <T> T fromJson(Codec<T> codec, JsonElement json) {
    return codec.parse(JsonOps.INSTANCE, json).getOrThrow(false, Tramways.LOGGER::error);
  }

  public static <T> JsonElement toJson(Codec<T> codec, T value) {
    return codec.encodeStart(JsonOps.INSTANCE, value).getOrThrow(false, Tramways.LOGGER::error);
  }
}
