package purplecreate.tramways.content.announcements.config;

import net.minecraft.network.chat.MutableComponent;
import purplecreate.tramways.Tramways;

public enum AnnouncementEvent {
  // train
  STOPPED_AT("On Stop At Station", true),
  APPROACHING_STATION("On Approach Station", true),
  DEPARTING_STATION("On Depart Station", true),

  // station
  THROUGH_TRAIN_APPROACH("On Through Train Approach", false),
  TERMINATING_TRAIN_APPROACH("On Terminating Train Approach", false),
  TERMINATING_TRAIN_STANDING("On Terminated Train Turnaround", false),
  PLATFORM_CHANGED("On Platform Change", false),
  TRAIN_DELAYED("On Train Delay", false),
  TRAIN_CANCELLED("On Train Cancellation", false);

  private final String lang;
  private final boolean train;

  AnnouncementEvent(String lang, boolean train) {
    this.lang = lang;
    this.train = train;
  }

  public static void registerLang() {
    for (AnnouncementEvent event : values()) {
      Tramways.REGISTRATE.addRawLang(
        Tramways.ID + ".announcements.event." + event.name().toLowerCase(),
        event.lang
      );
    }
  }

  public boolean isTrainEvent() {
    return train;
  }

  public MutableComponent translatable() {
    return Tramways.translatable("announcements.event." + name().toLowerCase());
  }
}
