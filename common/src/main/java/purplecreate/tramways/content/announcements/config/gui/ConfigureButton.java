package purplecreate.tramways.content.announcements.config.gui;

import com.simibubi.create.AllKeys;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class ConfigureButton extends AbstractButton {
  private final Runnable onPress;

  public ConfigureButton(int x, int y, int width, int height, Component message, Runnable onPress) {
    super(x, y, width, height, message);
    this.onPress = onPress;
  }

  @Override
  public void onPress() {
    onPress.run();
  }

  @Override
  protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    AllGuiTextures bg = isHovered ? AllKeys.isMouseButtonDown(0) ? AllGuiTextures.BUTTON_DOWN : AllGuiTextures.BUTTON_HOVER : AllGuiTextures.BUTTON;
    int textColor = (active ? 0xFFFFFF : 0xA0A0A0) | Mth.ceil(alpha * 255.0F) << 24;
    graphics.blitNineSliced(bg.location, getX(), getY(), width, height, 6, bg.getWidth(), bg.getHeight(), bg.getStartX(), bg.getStartY());
    renderString(graphics, Minecraft.getInstance().font, textColor);
  }

  @Override
  protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    defaultButtonNarrationText(narrationElementOutput);
  }
}
