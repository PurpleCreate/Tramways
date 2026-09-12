package purplecreate.tramways.content.announcements.engine.files;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import purplecreate.tramways.TNetworking;
import purplecreate.tramways.Tramways;
import purplecreate.tramways.content.announcements.network.RequestFilesC2SPacket;
import purplecreate.tramways.util.BiPacket;

import java.io.File;
import java.util.*;

public class ClientFileManager extends AbstractFileManager {
  private static ClientFileManager lastInstance;

  private final Minecraft mc;
  protected final Map<Integer, UploadHistoryItem> uploadHistory = new HashMap<>();

  private ClientFileManager(Minecraft mc) {
    this.mc = mc;
  }

  public static ClientFileManager getInstance() {
    if (lastInstance == null) {
      lastInstance = new ClientFileManager(Minecraft.getInstance());
    }

    return lastInstance;
  }

  @Override
  public File getFolder() {
    return mc.gameDirectory.toPath().resolve(Tramways.ID).resolve("session_cache").toFile();
  }

  @Override
  protected <T extends BiPacket> void send(Recipient recipient, T message) {
    TNetworking.sendToServer(message);
  }

  @Override
  public void onDestroy() {
    lastInstance = null;
  }

  @Override
  protected void onTransmissionStatusChange(Recipient recipient, FileInfo info, TransmissionStatus status, Component details) {
    int id = recipient.index();
    uploadHistory.put(id, new UploadHistoryItem(id, info, status, details));
  }

  public List<UploadHistoryItem> getUploadHistory() {
    return ImmutableList.copyOf(uploadHistory.values());
  }

  public void reportClientUploadError(FileInfo info, Component details) {
    int id = index++;
    uploadHistory.put(id, new UploadHistoryItem(id, info, TransmissionStatus.FAILED, details));
  }

  public void clearFinishedUploads() {
    uploadHistory.values().removeIf(item ->
      item.status == TransmissionStatus.FINISHED
        || item.status == TransmissionStatus.FAILED
    );
  }

  public void handleSyncPacket(List<FileInfo> serverFiles) {
    this.files.clear();

    String[] _downloadedFiles = getFolder().list();
    Set<String> downloadedFiles = _downloadedFiles == null ? Set.of() : Set.of(_downloadedFiles);
    Set<String> toRequest = new HashSet<>();

    for (FileInfo info : serverFiles) {
      if (downloadedFiles.contains(info.getRealLocation(getFolder()).getName())) {
        this.files.add(info);
      } else {
        toRequest.add(info.name());
      }
    }

    TNetworking.sendToServer(new RequestFilesC2SPacket(toRequest));
  }

  public record UploadHistoryItem(int id, FileInfo info, TransmissionStatus status, Component details) {
  }
}
