package purplecreate.tramways.content.announcements.station;

import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.simibubi.create.foundation.utility.CreateLang;
import de.mrjulsen.mcdragonlib.client.gui.widgets.base.DLWindow;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.createmod.catnip.data.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import purplecreate.tramways.TExtras;
import purplecreate.tramways.Tramways;
import purplecreate.tramways.content.announcements.config.AnnouncementConfig;
import purplecreate.tramways.content.announcements.config.gui.SelectAnnouncementEventWindow;
import purplecreate.tramways.content.announcements.config.gui.ConfigureButton;
import purplecreate.tramways.mixins.ModularGuiLineBuilderAccessor;

import java.util.*;

public class StationSpeakerDisplaySource extends DisplaySource {
  public static AnnouncementConfig getConfig(DisplayLinkContext context) {
    return AnnouncementConfig.fromNbt(context.sourceConfig(), false);
  }

  @ExpectPlatform
  private static void sendConfigurationPacket(BlockPos pos, CompoundTag configData, int targetLine) {
    throw new AssertionError();
  }

  @Override
  public void initConfigurationWidgets(DisplayLinkContext context, ModularGuiLineBuilder builder, boolean isFirstLine) {
    if (!(builder instanceof ModularGuiLineBuilderAccessor accessor)) return;

    if (isFirstLine) {
      builder.addTextInput(0, 137, (e, t) -> {
        e.setValue("");
        t.withTooltip(List.of(
          CreateLang.translateDirect("display_source.station_summary.filter")
            .withStyle((s) -> s.withColor(0x5391E1)),
          CreateLang.translateDirect("gui.schedule.lmb_edit")
            .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC)
        ));
      }, "Filter");
    } else {
      ConfigureButton button = new ConfigureButton(
        accessor.tramways$getX(), accessor.tramways$getY() - 4, 137, 16,
        Tramways.translatable("schedule.instruction.configure_announcements.button"),
        () -> {
          DLWindow.openWindow(m ->
            new SelectAnnouncementEventWindow(m, getConfig(context), (sourceData) -> {
              ResourceLocation id = TExtras.DisplaySources.STATION_SPEAKER.getId();
              sourceData.putString("Id", id.toString());
              sendConfigurationPacket(context.blockEntity().getBlockPos(), sourceData, 0);
            })
          );
        }
      );
      accessor.tramways$getTarget().add(Pair.of(button, "button"));
    }
  }

  @Override
  public void populateData(DisplayLinkContext context) {
    CompoundTag tag = context.sourceConfig();

    if (
      !tag.contains("Filter")
        && context.getSourceBlockEntity() instanceof StationBlockEntity be
        && be.getStation() != null
    ) {
      tag.putString("Filter", be.getStation().name);
    }
  }

  @Override
  public List<MutableComponent> provideText(DisplayLinkContext context, DisplayTargetStats stats) {
    return List.of();
  }
}
