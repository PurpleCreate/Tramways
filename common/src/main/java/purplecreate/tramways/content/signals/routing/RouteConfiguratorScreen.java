package purplecreate.tramways.content.signals.routing;

import com.simibubi.create.content.trains.signal.SignalBlockEntity;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.simibubi.create.foundation.gui.widget.TooltipArea;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import purplecreate.tramways.TGuiSprites;
import purplecreate.tramways.TNetworking;
import purplecreate.tramways.Tramways;
import purplecreate.tramways.content.signals.base.JunctionState;
import purplecreate.tramways.content.signals.base.JunctionState.DirectionalJunctionState;
import purplecreate.tramways.mixinInterfaces.IRoutedSignalBlock;

import java.util.List;

public class RouteConfiguratorScreen extends AbstractSimiScreen {
  private static final TGuiSprites bg = TGuiSprites.ROUTE_CONFIGURATOR;

  private static final MutableComponent title = Tramways.translatable("route_configurator.modal_title");
  private static final MutableComponent nameTitle = Tramways.translatable("route_configurator.name_title");
  private static final MutableComponent nameHint = Tramways.translatable("route_configurator.name_hint");
  private static final MutableComponent letterTitle = Tramways.translatable("route_configurator.letter_title");
  private static final MutableComponent letterHint = Tramways.translatable("route_configurator.letter_hint");
  private static final MutableComponent directionTitle = Tramways.translatable("route_configurator.direction_title");
  private static final MutableComponent directionHint = Tramways.translatable("route_configurator.direction_hint");

  private final SignalBlockEntity signal;
  private EditBox nameBox;
  private EditBox letterBox;
  private ScrollInput directionBox;

  public RouteConfiguratorScreen(SignalBlockEntity signal) {
    this.signal = signal;
  }

  @Override
  protected void init() {
    if (!(signal instanceof IRoutedSignalBlock routed)) return;
    JunctionState settings = routed.tramways$getRoute();

    setWindowSize(bg.getWidth(), bg.getHeight());
    super.init();

    addRenderableWidget(
      new IconButton(guiLeft + 228, guiTop + 57, AllIcons.I_CONFIRM)
        .withCallback(this::onClose)
    );

    nameBox = makeEditBox(58, 91, 64);
    nameBox.setValue(settings == null ? "" : settings.getName().getString());
    addRenderableWidget(
      new TooltipArea(guiLeft + 56, guiTop + 25, 95, 16)
        .withTooltip(List.of(
          nameTitle.plainCopy().withStyle((s) -> s.withColor(TooltipArea.HEADER_RGB.getRGB())),
          nameHint.plainCopy().withStyle((s) -> s.withColor(TooltipArea.HINT_RGB.getRGB()))
        ))
    );

    letterBox = makeEditBox(161, 12, 1);
    letterBox.setValue(settings == null ? "" : String.valueOf(settings.getLetter()));
    addRenderableWidget(
      new TooltipArea(guiLeft + 159, guiTop + 25, 16, 16)
        .withTooltip(List.of(
          letterTitle.plainCopy().withStyle((s) -> s.withColor(TooltipArea.HEADER_RGB.getRGB())),
          letterHint.plainCopy().withStyle((s) -> s.withColor(TooltipArea.HINT_RGB.getRGB()))
        ))
    );

    directionBox = new ScrollInput(guiLeft + 184, guiTop + 25, 16, 16)
      .titled(directionTitle)
      .addHint(directionHint)
      .inverted()
      .withShiftStep(1)
      .withRange(0, DirectionalJunctionState.values().length)
      .setState((settings == null ? DirectionalJunctionState.NONE : settings.getDirection()).ordinal());
    addRenderableWidget(directionBox);
  }

  private EditBox makeEditBox(int x, int width, int maxLength) {
    EditBox box = new EditBox(font, guiLeft + x, guiTop + 29, width, 10, Component.empty());
    box.setTextColor(-1);
    box.setTextColorUneditable(-1);
    box.setBordered(false);
    box.setMaxLength(maxLength);
    box.setFocused(false);
    box.mouseClicked(0, 0, 0);
    addRenderableWidget(box);
    return box;
  }

  @Override
  protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
    Font font = Minecraft.getInstance().font;

    bg.blit(graphics, guiLeft, guiTop);
    graphics.blit(bg.getPath(), guiLeft + 184, guiTop + 25, directionBox.getState() * 17, 85, 16, 16);
    graphics.drawString(font, title, Math.round(guiLeft + bg.getWidth() / 2f - font.width(title) / 2f), guiTop + 4, 0x442000, false);
  }

  @Override
  public void onClose() {
    super.onClose();

    String letter = letterBox.getValue();
    String name = nameBox.getValue();
    DirectionalJunctionState direction = DirectionalJunctionState.values()[directionBox.getState()];

    if (letter.isBlank() && name.isBlank()) {
      TNetworking.sendToServer(UpdateSignalC2SPacket.removeRoute(signal.getBlockPos()));
      return;
    }

    TNetworking.sendToServer(new UpdateSignalC2SPacket(signal.getBlockPos(), new JunctionState(
      letter.isBlank() ? '?' : letter.charAt(0),
      name.isBlank() ? Tramways.translatable("junction_state.unknown") : Component.literal(name),
      direction
    )));
  }
}
