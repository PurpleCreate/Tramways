package purplecreate.tramways;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public enum TGuiSprites {
  ROUTE_CONFIGURATOR("route_configurator", 256, 81),
  ITEM_CYCLE("route_configurator", 256, 103, 0, 105);

  private final ResourceLocation path;
  private final int width;
  private final int height;
  private final int offsetX;
  private final int offsetY;

  TGuiSprites(String name, int width, int height) {
    this(name, width, height, 0, 0);
  }

  TGuiSprites(String name, int width, int height, int offsetX, int offsetY) {
    this.path = Tramways.rl("textures/gui/" + name + ".png");
    this.width = width;
    this.height = height;
    this.offsetX = offsetX;
    this.offsetY = offsetY;
  }

  public ResourceLocation getPath() {
    return path;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public void blit(GuiGraphics graphics, int x, int y) {
    graphics.blit(path, x, y, offsetX, offsetY, width, height);
  }
}
