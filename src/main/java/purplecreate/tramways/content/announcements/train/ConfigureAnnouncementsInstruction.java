package purplecreate.tramways.content.announcements.train;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.content.trains.graph.DiscoveredPath;
import com.simibubi.create.content.trains.schedule.ScheduleRuntime;
import com.simibubi.create.content.trains.schedule.ScheduleScreen;
import com.simibubi.create.content.trains.schedule.destination.ScheduleInstruction;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import de.mrjulsen.mcdragonlib.client.gui.widgets.base.DLWindow;
import net.createmod.catnip.data.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import purplecreate.tramways.TBlocks;
import purplecreate.tramways.Tramways;
import purplecreate.tramways.content.announcements.config.AnnouncementConfig;
import purplecreate.tramways.content.announcements.config.AnnouncementEvent;
import purplecreate.tramways.content.announcements.config.gui.SelectAnnouncementEventWindow;
import purplecreate.tramways.content.announcements.config.gui.ConfigureButton;
import purplecreate.tramways.mixinInterfaces.ITram;
import purplecreate.tramways.mixins.ModularGuiLineBuilderAccessor;
import purplecreate.tramways.mixins.ScheduleScreenAccessor;

import java.util.ArrayList;
import java.util.List;

public class ConfigureAnnouncementsInstruction extends ScheduleInstruction {
  @Override
  public ResourceLocation getId() {
    return Tramways.rl("configure_announcements");
  }

  @Override
  public Pair<ItemStack, Component> getSummary() {
    return Pair.of(TBlocks.SPEAKER.asStack(), Tramways.translatable("schedule.instruction.configure_announcements"));
  }

  @Override
  public List<Component> getTitleAs(String type) {
    List<Component> tooltip = new ArrayList<>();

    tooltip.add(
      Tramways.translatable("schedule." + type + "." + this.getId().getPath())
        .withStyle(ChatFormatting.GOLD)
    );

    for (AnnouncementEvent event : AnnouncementEvent.values()) {
      int variants = getConfig().getVariants(event).size();
      if (variants == 0) continue;
      tooltip.add(
        Component.literal(" - ")
          .append(event.translatable())
          .append(": ")
          .append(
            Tramways.translatable("announcements.variant_count." + (variants == 1 ? "singular" : "plural"), variants)
              .withStyle(ChatFormatting.GRAY)
          )
      );
    }

    return tooltip;
  }

  public AnnouncementConfig getConfig() {
    return AnnouncementConfig.fromNbt(getData(), true);
  }

  @Override
  public void initConfigurationWidgets(ModularGuiLineBuilder builder) {
    if (!(builder instanceof ModularGuiLineBuilderAccessor accessor)) return;

    ConfigureButton button = new ConfigureButton(
      accessor.tramways$getX(), accessor.tramways$getY() - 4, 121, 16,
      Tramways.translatable("schedule.instruction.configure_announcements.button"),
      () -> {
        if (Minecraft.getInstance().screen instanceof ScheduleScreen screen) {
          ((ScheduleScreenAccessor) screen).tramways$getOnEditorClose().accept(true);
          DLWindow.openWindow(m ->
            new SelectAnnouncementEventWindow(m, getConfig(), (configData) -> {
              for (String key : configData.getAllKeys()) {
                getData().put(key, configData.get(key).copy());
              }
            })
          );
        }
      }
    );

    accessor.tramways$getTarget().add(Pair.of(button, "button"));
  }

  @Override
  public boolean supportsConditions() {
    return false;
  }

  @Override
  public @Nullable DiscoveredPath start(ScheduleRuntime runtime, Level level) {
    if (runtime.train instanceof ITram tram) {
      tram.tramways$setAnnouncementConfig(getConfig());
    }
    runtime.state = ScheduleRuntime.State.PRE_TRANSIT;
    runtime.currentEntry++;
    return null;
  }
}
