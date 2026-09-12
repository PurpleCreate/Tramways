package purplecreate.tramways.content.announcements.config.gui;

import de.mrjulsen.crn.client.gui.CreateDynamicWidgets;
import de.mrjulsen.crn.client.gui.CreateDynamicWidgets.BarColor;
import de.mrjulsen.crn.client.gui.CreateDynamicWidgets.ContainerColor;
import de.mrjulsen.crn.client.gui.CreateDynamicWidgets.FooterSize;
import de.mrjulsen.mcdragonlib.client.gui.widgets.base.DLWindow;
import de.mrjulsen.mcdragonlib.client.gui.widgets.base.DLWindowManager;
import de.mrjulsen.mcdragonlib.client.util.DLGuiGraphics;
import de.mrjulsen.mcdragonlib.client.util.GuiUtils;
import de.mrjulsen.mcdragonlib.data.ETextAlignment;
import de.mrjulsen.mcdragonlib.util.DLColor;
import de.mrjulsen.mcdragonlib.util.math.Rectangle;
import net.minecraft.network.chat.Component;

public class AbstractTitledWindow extends DLWindow {
  protected static final int GUI_WIDTH = 240;
  protected static final int GUI_HEIGHT = 247;

  private final Component title;

  public AbstractTitledWindow(DLWindowManager manager, Component title) {
    super(manager);
    this.title = title;
  }

  @Override
  public void renderMainLayer(DLGuiGraphics graphics, double mouseX, double mouseY, Rectangle renderBounds) {
    CreateDynamicWidgets.renderWindow(graphics, 0, 0, GUI_WIDTH, GUI_HEIGHT, ContainerColor.GRAY, BarColor.GRAY, FooterSize.DEFAULT.size(), FooterSize.SMALL.size(), true);
    GuiUtils.drawString(graphics, graphics.defaultFont(), 6, 4, title, DLColor.fromInt(0xFF4F4F4F), ETextAlignment.LEFT, false);
  }
}
