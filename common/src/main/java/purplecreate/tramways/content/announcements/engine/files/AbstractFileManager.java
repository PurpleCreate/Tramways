package purplecreate.tramways.content.announcements.engine.files;

import com.google.common.collect.ImmutableList;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import purplecreate.tramways.Tramways;
import purplecreate.tramways.config.TConfigs;
import purplecreate.tramways.content.announcements.network.StartTransmittingBiPacket;
import purplecreate.tramways.content.announcements.network.TransmitChunkBiPacket;
import purplecreate.tramways.content.announcements.network.UpdateStatusBiPacket;
import purplecreate.tramways.util.BiPacket;

import java.io.*;
import java.util.*;

public abstract class AbstractFileManager {
  protected final List<FileInfo> files = new ArrayList<>();

  private final Map<Recipient, InputStream> transmitting = new HashMap<>();
  private final Map<Recipient, TransmissionStatus> transmissionStatus = new HashMap<>();
  private final Map<Recipient, FileInfo> transmittingInfo = new HashMap<>();
  private final Map<Recipient, Integer> transmittingOffset = new HashMap<>();
  private final Map<Recipient, byte[]> receiving = new HashMap<>();
  private final Map<Recipient, FileInfo> receivingInfo = new HashMap<>();
  private final Map<Recipient, Integer> receivedLength = new HashMap<>();

  private boolean initialized = false;
  private boolean destroyed = false;
  protected int index = 0;

  public abstract File getFolder();
  protected abstract <T extends BiPacket> void send(Recipient recipient, T message);

  public void init() {
    if (initialized) throw new RuntimeException("Already initialised");
    initialized = true;
    getFolder().mkdirs();
    onInit();
  }

  public void destroy() {
    if (destroyed) throw new RuntimeException("Already destroyed");
    destroyed = true;
    onDestroy();
  }

  public void tick() {
    transmit();
  }

  protected void onInit() {}
  protected void onDestroy() {}
  protected void onFilesChange() {}
  protected void onTransmissionStatusChange(Recipient recipient, FileInfo info, TransmissionStatus status, Component details) {}

  public List<FileInfo> getFiles() {
    return ImmutableList.copyOf(files);
  }

  public @Nullable FileInfo getFile(String name) {
    for (FileInfo info : files) {
      if (info.name().equalsIgnoreCase(name)) return info;
    }

    return null;
  }

  public void startTransmittingFromDatabase(@Nullable Player player, FileInfo info) {
    try {
      InputStream stream = new FileInputStream(info.getRealLocation(getFolder()));
      startTransmitting(player, info, stream, false);
    } catch (IOException e) {
      Tramways.LOGGER.warn("An IOException occurred whilst trying to read from {}", info.name(), e);
    }
  }

  public void startTransmitting(@Nullable Player player, FileInfo info, InputStream stream, boolean adminMode) {
    UUID playerId = player == null ? null : player.getGameProfile().getId();
    Recipient recipient = new Recipient(playerId, index++);

    onTransmissionStatusChange(recipient, info, TransmissionStatus.QUEUED, Component.empty());
    transmissionStatus.put(recipient, TransmissionStatus.QUEUED);

    transmitting.put(recipient, stream);
    transmittingInfo.put(recipient, info);
    transmittingOffset.put(recipient, 0);
    send(recipient, new StartTransmittingBiPacket(recipient.index, info, adminMode));
  }

  public void setTransmissionStatus(@Nullable Player player, int id, TransmissionStatus status, Component details) {
    UUID playerId = player == null ? null : player.getGameProfile().getId();
    Recipient recipient = new Recipient(playerId, id);

    FileInfo info = transmittingInfo.get(recipient);
    if (info == null) return;

    onTransmissionStatusChange(recipient, info, status, details);
    transmissionStatus.put(recipient, status);

    if (status.shouldRemove) {
      removeTransmission(recipient);
    }
  }

  public void transmit() {
    int maxChunkSize = TConfigs.server().fileChunkSizeBytes.get();

    Set<Map.Entry<Recipient, InputStream>> entries = transmitting.entrySet();
    for (Map.Entry<Recipient, InputStream> entry : entries) {
      Recipient recipient = entry.getKey();
      InputStream stream = entry.getValue();
      int offset = transmittingOffset.get(recipient);

      if (!transmissionStatus.get(recipient).shouldTransmit)
        continue;

      try {
        int length = 0;
        byte[] chunk = new byte[maxChunkSize];
        for (int i = 0; i < maxChunkSize; i++) {
          int b = stream.read();

          if (b == -1) {
            break;
          }

          length++;
          chunk[i] = (byte)(b & 0xFF);
        }
        chunk = Arrays.copyOfRange(chunk, 0, length);

        transmittingOffset.put(recipient, offset + chunk.length);
        send(recipient, new TransmitChunkBiPacket(recipient.index, offset, chunk));
      } catch (IOException e) {
        Tramways.LOGGER.warn("An IOException occurred whilst transmitting from an InputStream", e);
      }
    }
  }

  private void removeTransmission(Recipient recipient) {
    transmissionStatus.remove(recipient);
    transmittingInfo.remove(recipient);
    transmittingOffset.remove(recipient);
    InputStream stream = transmitting.remove(recipient);

    if (stream != null) {
      try {
        stream.close();
      } catch (IOException e) {
        Tramways.LOGGER.warn("An IOException occurred whilst closing a transmission", e);
      }
    }
  }

  public void startReceiving(@Nullable Player player, FileInfo info, int id, boolean requestAdminMode) {
    UUID playerId = player == null ? null : player.getGameProfile().getId();
    Recipient recipient = new Recipient(playerId, id);

    Component rejectionReason = null;
    int playerFileCount = 0;
    for (FileInfo other : files) {
      if (other.ownerId().equals(playerId)) {
        playerFileCount++;
      }

      if (other.name().equals(info.name())) {
        rejectionReason = Tramways.translatable("announcements.file_manager.details.file_exists_same_name");
        break;
      }

      if (other.integrityType() == info.integrityType() && Arrays.equals(other.integrity(), info.integrity())) {
        rejectionReason = Tramways.translatable("announcements.file_manager.details.file_exists_diff_name", other.name());
        break;
      }
    }

    if (
      rejectionReason == null
        && player != null
        && (!requestAdminMode || !TConfigs.server().adminsBypassLimit.get() || !player.hasPermissions(2))
    ) {
      int countLimit = TConfigs.server().fileCountLimit.get();
      int sizeLimit = TConfigs.server().fileSizeLimitKbytes.get();
      int sizeLimitBytes = sizeLimit * 1000;

      if (playerFileCount >= countLimit) {
        rejectionReason = Tramways.translatable("announcements.file_manager.details.too_many_files", countLimit);
      } else if (info.fileSize() > sizeLimitBytes) {
        rejectionReason = Tramways.translatable("announcements.file_manager.details.file_too_big", sizeLimit);
      }
    }

    if (rejectionReason != null) {
      send(recipient, new UpdateStatusBiPacket(id, TransmissionStatus.FAILED, rejectionReason));
      return;
    }

    receiving.put(recipient, new byte[info.fileSize()]);
    receivingInfo.put(recipient, info.copyWithPlayer(player));
    receivedLength.put(recipient, 0);
    send(recipient, new UpdateStatusBiPacket(id, TransmissionStatus.PROGRESSING, Component.literal("0%")));
  }

  public void receive(@Nullable Player player, int id, int offset, byte[] chunk) {
    UUID playerId = player == null ? null : player.getGameProfile().getId();
    Recipient recipient = new Recipient(playerId, id);

    byte[] buffer = receiving.get(recipient);
    FileInfo info = receivingInfo.get(recipient);
    Integer length = receivedLength.get(recipient);

    if (buffer == null || info == null || length == null || length >= info.fileSize()) return;

    for (int i = 0; i < chunk.length; i++) {
      buffer[offset + i] = chunk[i];
      length++;

      if (length >= info.fileSize()) {
        writeFile(recipient, info, buffer);
        receiving.remove(recipient);
        receivingInfo.remove(recipient);
        receivedLength.remove(recipient);
        return;
      }
    }

    receivedLength.put(recipient, length);

    int percentage = Mth.floor((length / (double)info.fileSize()) * 100);
    send(recipient, new UpdateStatusBiPacket(id, TransmissionStatus.PROGRESSING, Component.literal(percentage + "%")));
  }

  private void writeFile(Recipient recipient, FileInfo info, byte[] buffer) {
    byte[] calculatedIntegrity = info.integrityType().get().digest(buffer);
    if (!Arrays.equals(info.integrity(), calculatedIntegrity)) {
      send(recipient, new UpdateStatusBiPacket(recipient.index, TransmissionStatus.FAILED, Tramways.translatable("announcements.file_manager.details.corrupted")));
      Tramways.LOGGER.warn("Received file corrupted");
      return;
    }

    try (OutputStream stream = new FileOutputStream(info.getRealLocation(getFolder()))) {
      stream.write(buffer);
    } catch (IOException e) {
      send(recipient, new UpdateStatusBiPacket(recipient.index, TransmissionStatus.FAILED, Tramways.translatable("announcements.file_manager.details.internal_error")));
      Tramways.LOGGER.warn("An IOException occurred whilst writing to {}", info.name(), e);
      return;
    }

    files.add(info);
    onFilesChange();
    send(recipient, new UpdateStatusBiPacket(recipient.index, TransmissionStatus.FINISHED, null));
  }

  protected record Recipient(@Nullable UUID playerId, Integer index) {
    @Override
    public int hashCode() {
      int a = playerId == null ? 0 : playerId.hashCode();
      int b = index.hashCode();
      return a * 31 ^ b;
    }

    @Override
    public String toString() {
      return playerId + ":" + index;
    }
  }
}
