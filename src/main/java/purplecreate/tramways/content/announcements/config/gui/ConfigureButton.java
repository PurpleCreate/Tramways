package purplecreate.tramways.content.announcements.config.gui;

import com.simibubi.create.AllKeys;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import purplecreate.tramways.Tramways;

public class ConfigureButton extends AbstractButton {
  private static final ResourceLocation SPRITE = Tramways.rl("create_button");
  private static final ResourceLocation SPRITE_HOVER = Tramways.rl("create_button_hover");
  private static final ResourceLocation SPRITE_DOWN = Tramways.rl("create_button_down");

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
    ResourceLocation bg = isHovered ? AllKeys.isMouseButtonDown(0) ? SPRITE_DOWN : SPRITE_HOVER : SPRITE;
    int textColor = (active ? 0xFFFFFF : 0xA0A0A0) | Mth.ceil(alpha * 255.0F) << 24;
    graphics.blitSprite(bg, getX(), getY(), width, height);
    renderString(graphics, Minecraft.getInstance().font, textColor);
  }

  @Override
  protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    defaultButtonNarrationText(narrationElementOutput);
  }
}
