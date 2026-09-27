package purplecreate.tramways.content.announcements.station;

import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.api.behaviour.display.DisplayTarget;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import purplecreate.tramways.Tramways;
import purplecreate.tramways.content.announcements.SpeakerBlockEntity;
import purplecreate.tramways.content.announcements.config.AnnouncementConfig;

import java.util.*;

public class SpeakerDisplayTarget extends DisplayTarget {
  @Override
  public void acceptText(int i, List<MutableComponent> list, DisplayLinkContext context) {
    CompoundTag tag = context.sourceConfig();

    if (context.getTargetBlockEntity() instanceof SpeakerBlockEntity speaker) {
      speaker.configure(tag.getString("Filter"), AnnouncementConfig.fromNbt(tag, false));
    }
  }

  @Override
  public DisplayTargetStats provideStats(DisplayLinkContext displayLinkContext) {
    return new DisplayTargetStats(1, 1, this);
  }

  @Override
  public Component getLineOptionText(int line) {
    return Tramways.translatable("display_target.speaker");
  }
}
