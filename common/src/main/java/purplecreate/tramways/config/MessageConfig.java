package purplecreate.tramways.config;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import net.minecraft.network.FriendlyByteBuf;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class MessageConfig implements JsonSerializer<MessageConfig>, JsonDeserializer<MessageConfig> {
  private boolean legacy;
  private String type;
  private List<String> message;

  public static MessageConfig simple(String message) {
    MessageConfig messageConfig = new MessageConfig();
    messageConfig.legacy = true;
    messageConfig.type = "tts";
    messageConfig.message = List.of(message);
    return messageConfig;
  }

  public String getType() {
    return type;
  }

  public List<String> getMessage() {
    return message;
  }

  public MessageConfig applyProperties(Map<String, String> props) {
    MessageConfig messageConfig = new MessageConfig();
    messageConfig.legacy = false;
    messageConfig.type = this.type;
    messageConfig.message = this.message
      .stream()
      .map((part) ->
        Pattern
          .compile("\\$(?:([a-z_:]+)|\\{([a-z_:]+)})")
          .matcher(part)
          .replaceAll((result) -> {
            String g1 = result.group(1);
            String g2 = result.group(2);
            String value = props.getOrDefault(g1 != null ? g1 : g2, "");
            if ("files".equals(type))
              value = value.toLowerCase().replaceAll("[^a-z0-9_./-]+", "_");
            return value;
          })
      )
      .toList();
    return messageConfig;
  }

  public static MessageConfig readBytes(FriendlyByteBuf buf) {
    MessageConfig messageConfig = new MessageConfig();
    messageConfig.legacy = false;
    messageConfig.type = buf.readUtf();
    List<String> message = new ArrayList<>();
    int length = buf.readVarInt();
    for (int i = 0; i < length; i++)
      message.add(buf.readUtf());
    messageConfig.message = message;
    return messageConfig;
  }

  public void writeBytes(FriendlyByteBuf buf) {
    buf.writeUtf(type);
    buf.writeVarInt(message.size());
    for (String part : message)
      buf.writeUtf(part);
  }

  @Override
  public JsonElement serialize(MessageConfig messageConfig, Type type, JsonSerializationContext context) {
    if (messageConfig.legacy)
      return new JsonPrimitive(
        messageConfig.message.isEmpty()
          ? ""
          : messageConfig.message.get(0)
      );

    JsonObject object = new JsonObject();
    object.add("type", new JsonPrimitive(messageConfig.type));
    object.add(
      "message",
      messageConfig.message.size() == 1
        ? new JsonPrimitive(messageConfig.message.get(0))
        : Config.gson.toJsonTree(messageConfig.message)
    );
    return object;
  }

  @Override
  public MessageConfig deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
    MessageConfig messageConfig = new MessageConfig();

    if (jsonElement.isJsonObject()) {
      JsonObject object = jsonElement.getAsJsonObject();
      JsonElement typeString = object.get("type");
      JsonElement message = object.get("message");

      messageConfig.legacy = false;
      messageConfig.type = typeString.getAsString();
      messageConfig.message = message.isJsonPrimitive()
        ? List.of(message.getAsString())
        : Config.gson.fromJson(message, new TypeToken<>() {});
    } else {
      messageConfig.legacy = true;
      messageConfig.type = "tts";
      messageConfig.message = List.of(jsonElement.getAsString());
    }

    return messageConfig;
  }
}
