package purplecreate.tramways.content.signals;

import com.simibubi.create.api.behaviour.display.DisplayTarget;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import purplecreate.tramways.Tramways;
import purplecreate.tramways.content.signals.block.GenericSignalBlockEntity;

import java.util.List;

public class SignalDisplayTarget extends DisplayTarget {
  @Override
  public void acceptText(int i, List<MutableComponent> list, DisplayLinkContext context) {
    if (!(context.getTargetBlockEntity() instanceof GenericSignalBlockEntity be)) return;
    be.bindDisplayLink(context.blockEntity());
  }

  @Override
  public DisplayTargetStats provideStats(DisplayLinkContext displayLinkContext) {
    return new DisplayTargetStats(1, 1, this);
  }

  @Override
  public Component getLineOptionText(int line) {
    return Tramways.translatable("display_target.signal");
  }
}
