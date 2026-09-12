package purplecreate.tramways.content.announcements.engine.files;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.LevelResource;
import purplecreate.tramways.TNetworking;
import purplecreate.tramways.Tramways;
import purplecreate.tramways.content.announcements.network.DatabaseSyncS2CPacket;
import purplecreate.tramways.util.BiPacket;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.ListIterator;
import java.util.Objects;

public class ServerFileManager extends AbstractFileManager {
  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

  private static MinecraftServer lastServer;
  private static ServerFileManager lastInstance;

  private final MinecraftServer server;
  private boolean dirty;

  private ServerFileManager(MinecraftServer server) {
    this.server = server;
  }

  public static ServerFileManager getInstance(MinecraftServer server) {
    if (lastServer == server) {
      return lastInstance;
    }

    if (lastInstance != null) {
      lastInstance.destroy();
    }

    lastServer = server;
    lastInstance = new ServerFileManager(server);

    return lastInstance;
  }

  @Override
  public File getFolder() {
    return server.getWorldPath(new LevelResource(Tramways.ID)).resolve("file_storage").toFile();
  }

  @Override
  protected <T extends BiPacket> void send(Recipient recipient, T message) {
    Objects.requireNonNull(recipient.playerId());
    TNetworking.sendToPlayer(message, server.getPlayerList().getPlayer(recipient.playerId()));
  }

  private File getDatabaseFile() {
    return new File(getFolder(), "db.json");
  }

  @Override
  public void onInit() {
    try (FileReader reader = new FileReader(getDatabaseFile())) {
      JsonArray array = GSON.fromJson(reader, JsonArray.class);
      if (array != null) {
        for (JsonElement element : array) {
          files.add(FileInfo.fromJson(element));
        }
      }
    } catch (IOException e) {
      Tramways.LOGGER.warn("An IOException occurred whilst reading the files database", e);
    }
  }

  @Override
  public void onDestroy() {
    lastServer = null;
    lastInstance = null;
    save();
  }

  @Override
  protected void onFilesChange() {
    dirty = true;
    TNetworking.sendToAll(new DatabaseSyncS2CPacket(files));
  }

  public void save() {
    if (dirty) {
      try (FileWriter writer = new FileWriter(getDatabaseFile())) {
        JsonArray array = new JsonArray();
        for (FileInfo file : files) {
          array.add(file.toJson());
        }
        GSON.toJson(array, writer);

        dirty = false;
      } catch (IOException e) {
        Tramways.LOGGER.warn("An IOException occurred whilst writing the files database", e);
      }
    }
  }

  public void renameFile(Player player, FileInfo info, String newName) {
    for (ListIterator<FileInfo> it = files.listIterator(); it.hasNext(); ) {
      FileInfo other = it.next();

      if (other.integrityType().equals(info.integrityType()) && Arrays.equals(other.integrity(), info.integrity())) {
        if (!other.ownerId().equals(player.getGameProfile().getId()) && !player.hasPermissions(2)) break;

        it.set(info.copyWithName(newName));
        onFilesChange();
        break;
      }
    }
  }

  public void deleteFile(Player player, FileInfo info) {
    for (ListIterator<FileInfo> it = files.listIterator(); it.hasNext(); ) {
      FileInfo other = it.next();

      if (other.integrityType().equals(info.integrityType()) && Arrays.equals(other.integrity(), info.integrity())) {
        if (!other.ownerId().equals(player.getGameProfile().getId()) && !player.hasPermissions(2)) break;

        other.getRealLocation(getFolder()).delete();
        it.remove();
        onFilesChange();
        break;
      }
    }
  }

  public void handlePlayerJoin(ServerPlayer player) {
    for (ListIterator<FileInfo> it = files.listIterator(); it.hasNext(); ) {
      FileInfo info = it.next();
      if (info.ownerId() == player.getGameProfile().getId() && !info.owner().equals(player.getGameProfile().getName())) {
        it.set(info.copyWithPlayer(player));
        dirty = true;
      }
    }

    TNetworking.sendToPlayer(new DatabaseSyncS2CPacket(files), player);
  }
}
