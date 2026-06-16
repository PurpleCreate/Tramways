package purplecreate.tramways.content.announcements.engine.files;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.audio.OggAudioStream;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import purplecreate.tramways.Tramways;
import purplecreate.tramways.content.announcements.engine.MP3AudioStream;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.UUID;

public record FileInfo(
  String name,
  Type fileType,
  Hash integrityType,
  byte[] integrity,
  int fileSize,
  String owner,
  UUID ownerId
) {
  public static FileInfo determine(File file, InputStream stream) throws DeterminationException {
    String name = file.getName();
    byte[] buffer;
    try {
      stream.mark(Integer.MAX_VALUE);
      buffer = stream.readAllBytes();
      stream.reset();
    } catch (IOException e) {
      throw new DeterminationException(Tramways.translatable("announcements.file_manager.details.client_read_error"), fileNameOnly(name));
    }

    Type type = null;
    for (Type tryType : Type.values()) {
      if (tryType == Type.UNKNOWN) continue;

      int extStart = name.length() - tryType.extension.length();
      int extEnd = name.length();

      if (name.substring(extStart, extEnd).equalsIgnoreCase(tryType.extension)) {
        name = name.substring(0, extStart);
        type = tryType;
        break;
      }
    }

    if (type == null) {
      throw new DeterminationException(Tramways.translatable("announcements.file_manager.details.client_invalid_type"), fileNameOnly(name));
    }

    return new FileInfo(
      name,
      type,
      Hash.SHA256,
      Hash.SHA256.get().digest(buffer),
      buffer.length,
      "",
      new UUID(0, 0)
    );
  }

  public static FileInfo fileNameOnly(String name) {
    return new FileInfo(
      name,
      Type.UNKNOWN,
      Hash.SHA256,
      new byte[0],
      0,
      "",
      new UUID(0, 0)
    );
  }

  public static FileInfo fromNetwork(FriendlyByteBuf buf) {
    return new FileInfo(
      buf.readUtf(),
      buf.readEnum(Type.class),
      buf.readEnum(Hash.class),
      buf.readByteArray(),
      buf.readVarInt(),
      buf.readUtf(),
      buf.readUUID()
    );
  }

  public void toNetwork(FriendlyByteBuf buf) {
    buf.writeUtf(name);
    buf.writeEnum(fileType);
    buf.writeEnum(integrityType);
    buf.writeByteArray(integrity);
    buf.writeVarInt(fileSize);
    buf.writeUtf(owner);
    buf.writeUUID(ownerId);
  }

  public static FileInfo fromJson(JsonElement json) {
    JsonObject object = json.getAsJsonObject();

    return new FileInfo(
      object.get("name").getAsString(),
      Type.valueOf(object.get("fileType").getAsString()),
      Hash.valueOf(object.get("integrityType").getAsString()),
      HexFormat.of().parseHex(object.get("integrity").getAsString()),
      object.get("fileSize").getAsInt(),
      object.get("owner").getAsString(),
      UUID.fromString(object.get("ownerId").getAsString())
    );
  }

  public JsonElement toJson() {
    JsonObject object = new JsonObject();

    object.addProperty("name", name);
    object.addProperty("fileType", fileType.name());
    object.addProperty("integrityType", integrityType.name());
    object.addProperty("integrity", HexFormat.of().formatHex(integrity));
    object.addProperty("fileSize", fileSize);
    object.addProperty("owner", owner);
    object.addProperty("ownerId", ownerId.toString());

    return object;
  }

  public File getRealLocation(File folder) {
    return new File(folder, integrityType.name() + "-" + HexFormat.of().formatHex(integrity));
  }

  public FileInfo copyWithName(String name) {
    return new FileInfo(
      name,
      fileType,
      integrityType,
      integrity,
      fileSize,
      owner,
      ownerId
    );
  }

  public FileInfo copyWithPlayer(@Nullable Player player) {
    String playerName = "Server";
    UUID playerId = new UUID(0, 0);

    if (player != null) {
      playerName = player.getGameProfile().getName();
      playerId = player.getGameProfile().getId();
    }

    return new FileInfo(
      name,
      fileType,
      integrityType,
      integrity,
      fileSize,
      playerName,
      playerId
    );
  }

  public enum Type {
    UNKNOWN("") {
      @Override
      public AudioStream createAudioStream(InputStream stream) throws IOException {
        throw new RuntimeException("No implementation");
      }
    },
    OPUS(".ogg") {
      @Override
      public AudioStream createAudioStream(InputStream stream) throws IOException {
        return new OggAudioStream(stream);
      }
    },
    MPEG(".mp3") {
      @Override
      public AudioStream createAudioStream(InputStream stream) throws IOException {
        return new MP3AudioStream(stream);
      }
    };

    private final String extension;

    Type(String extension) {
      this.extension = extension;
    }

    public abstract AudioStream createAudioStream(InputStream stream) throws IOException;

    public static String[] getPatterns() {
      return Arrays.stream(values()).map(t -> "*" + t.extension).toArray(String[]::new);
    }

    public String getExtension() {
      return extension;
    }
  }

  public enum Hash {
    SHA256("SHA-256");

    private final String algorithm;

    Hash(String name) {
      this.algorithm = name;
    }

    public MessageDigest get() {
      try {
        return MessageDigest.getInstance(algorithm);
      } catch (NoSuchAlgorithmException e) {
        throw new RuntimeException(e);
      }
    }
  }

  public static class DeterminationException extends Exception {
    public final Component details;
    public final FileInfo partialInfo;

    public DeterminationException(Component details, FileInfo partialInfo) {
      this.details = details.copy();
      this.partialInfo = partialInfo;
    }
  }
}
