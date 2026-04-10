package purplecreate.tramways.content.signals.routing;

import com.simibubi.create.content.trains.signal.SignalBlockEntity;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import purplecreate.tramways.TGuiSprites;
import purplecreate.tramways.TNetworking;
import purplecreate.tramways.content.signals.base.JunctionState;
import purplecreate.tramways.content.signals.base.JunctionState.DirectionalJunctionState;
import purplecreate.tramways.mixinInterfaces.IRoutedSignalBlock;

public class RouteConfiguratorScreen extends AbstractSimiScreen {
  private static final TGuiSprites bg = TGuiSprites.ROUTE_CONFIGURATOR;

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

    nameBox = makeEditBox(56, 95, 64);
    nameBox.setValue(settings == null ? "" : settings.getName().getString());

    letterBox = makeEditBox(159, 16, 1);
    letterBox.setValue(settings == null ? "" : String.valueOf(settings.getLetter()));

    directionBox = new ScrollInput(guiLeft + 184, guiTop + 24, 16, 16)
      .inverted()
      .withRange(0, DirectionalJunctionState.values().length)
      .setState((settings == null ? DirectionalJunctionState.NONE : settings.getDirection()).ordinal());
    addRenderableWidget(directionBox);
  }

  private EditBox makeEditBox(int x, int width, int maxLength) {
    EditBox box = new EditBox(font, guiLeft + x, guiTop + 28, width, 10, Component.empty());
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
    bg.blit(graphics, 0, 0);
    graphics.blit(bg.getPath(), guiLeft + 184, guiTop + 24, directionBox.getState() * 17, 85, 16, 16);
  }

  @Override
  public void onClose() {
    super.onClose();

    JunctionState defaults = JunctionState.unknown();
    String letter = letterBox.getValue();
    String name = nameBox.getValue();
    DirectionalJunctionState direction = DirectionalJunctionState.values()[directionBox.getState()];

    if (letter.isBlank() && name.isBlank()) {
      TNetworking.sendToServer(UpdateSignalC2SPacket.removeRoute(signal.getBlockPos()));
      return;
    }

    TNetworking.sendToServer(new UpdateSignalC2SPacket(signal.getBlockPos(), new JunctionState(
      letter.isBlank() ? defaults.getLetter() : letter.charAt(0),
      name.isBlank() ? defaults.getName() : Component.literal(name),
      direction
    )));
  }
}
