package purplecreate.tramways.content.announcements.engine.files;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import purplecreate.tramways.Tramways;

public enum TransmissionStatus {
  QUEUED(false, false),
  FAILED(false, true),
  PROGRESSING(true, false),
  FINISHED(false, true);

  public final boolean shouldTransmit;
  public final boolean shouldRemove;

  TransmissionStatus(boolean shouldTransmit, boolean shouldRemove) {
    this.shouldTransmit = shouldTransmit;
    this.shouldRemove = shouldRemove;
  }

  public MutableComponent translatable() {
    return Tramways.translatable("announcements.file_manager.status." + name().toLowerCase());
  }

  public MutableComponent translatable(Component detail) {
    return Tramways.translatable("announcements.file_manager.status." + name().toLowerCase() + ".tooltip", detail);
  }
}
