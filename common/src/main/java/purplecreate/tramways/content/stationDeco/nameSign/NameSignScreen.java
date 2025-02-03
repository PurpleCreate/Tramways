package purplecreate.tramways.content.stationDeco.nameSign;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.gui.AbstractSimiScreen;
import com.simibubi.create.foundation.utility.RegisteredObjects;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import org.lwjgl.glfw.GLFW;
import purplecreate.tramways.TBlocks;
import purplecreate.tramways.TNetworking;
import purplecreate.tramways.Tramways;
import purplecreate.tramways.content.stationDeco.nameSign.info.NameSignInfo;
import purplecreate.tramways.content.stationDeco.nameSign.network.UpdateNameSignC2SPacket;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class NameSignScreen extends AbstractSimiScreen {
  private static final Map<ResourceLocation, ResourceLocation> textureMap = new HashMap<>();
  // 1 pixel == 8 units here for some reason
  private static final int textureWidth = 16 * 8;
  private static final int textureHeight = 8 * 8;
  private static final int textureStartX = 0;
  private static final int textureStartY = 0;
  private int ticks = 0;

  static {
    for (BlockEntry<NameSignBlock> block : TBlocks.STATION_NAME_SIGNS) {
      String name = block.getId().getPath();
      String color = name.substring(0, name.length() - 18);

      textureMap.put(block.getId(), Tramways.rl("textures/block/station_name_sign/" + color + ".png"));
    }
  }

  private final NameSignBlockEntity be;
  private final ResourceLocation background;
  private final NameSignInfo.Entry nameSignInfo;

  private List<String> lines;
  private int currentLine = 0;
  private TextFieldHelper field;

  public NameSignScreen(NameSignBlockEntity be) {
    super(Component.translatable("sign.edit"));

    Block block = be.getBlockState().getBlock();
    this.be = be;
    this.lines = be.getLinesSafe();
    this.background = textureMap.get(BuiltInRegistries.BLOCK.getKey(block));
    this.nameSignInfo = NameSignInfo.get(RegisteredObjects.getKeyOrThrow(block));
  }

  @Override
  protected void init() {
    setWindowSize(textureWidth, height);
    super.init();

    this.addRenderableWidget(
      Button.builder(CommonComponents.GUI_DONE, (arg) -> minecraft.setScreen(null))
        .bounds(guiLeft, windowHeight - 40 - 20, windowWidth, 20).build()
    );
    this.field = new TextFieldHelper(
      () -> lines.get(currentLine),
      (text) -> {
        lines.set(currentLine, text);
        TNetworking.sendToServer(new UpdateNameSignC2SPacket(be.getBlockPos(), lines));
      },
      TextFieldHelper.createClipboardGetter(minecraft),
      TextFieldHelper.createClipboardSetter(minecraft),
      (text) -> minecraft.font.width(text) <= be.textWidth
    );
  }

  @Override
  public void tick() {
    ticks++;
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    if (keyCode == GLFW.GLFW_KEY_UP) {
      currentLine = currentLine - 1 & 3;
      field.setCursorToEnd();
      return true;
    } else if (keyCode == GLFW.GLFW_KEY_DOWN) {
      currentLine = currentLine + 1 & 3;
      field.setCursorToEnd();
      return true;
    } else if (keyCode != GLFW.GLFW_KEY_ESCAPE) {
      return field.keyPressed(keyCode);
    }
    return super.keyPressed(keyCode, scanCode, modifiers);
  }

  @Override
  public boolean charTyped(char codePoint, int modifiers) {
    field.charTyped(codePoint);
    return true;
  }

  @Override
  protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
    PoseStack ms = graphics.pose();
    Font fontRenderer = minecraft.font;

    // background
    graphics.blit(
      background,
      guiLeft,
      guiTop + ((windowHeight / 2) - (textureHeight / 2)),
      textureStartX,
      textureStartY,
      textureWidth,
      textureHeight
    );

    // title
    graphics.drawCenteredString(fontRenderer, title, guiLeft + (windowWidth / 2), 40, 16777215);

    // text
    int cursorRow = currentLine;
    int cursorCol = field.getCursorPos();
    boolean showCursor = ticks / 6 % 2 != 0;

    float tx = switch (nameSignInfo.align()) {
      case LEFT -> 0f;
      case RIGHT -> 1f;
      case CENTER -> .5f;
    };

    ms.pushPose();
    ms.translate(guiLeft, guiTop, 0);
    ms.translate(windowWidth * tx, windowHeight / 2f, 0);
    NameSignRenderer.renderText(
      nameSignInfo,
      lines,
      ms,
      graphics.bufferSource(),
      LightTexture.FULL_BRIGHT,
      false
    );

    // cursor
    if (showCursor) {
      String line = lines.isEmpty()
        ? ""
        : lines.get(cursorRow);

      int x = switch (nameSignInfo.align()) {
        case LEFT -> nameSignInfo.offset();
        case RIGHT -> -nameSignInfo.offset();
        case CENTER -> (fontRenderer.width(line) / -2) + nameSignInfo.offset();
      };
      int y = (int) (cursorRow * fontRenderer.lineHeight - (lines.size() * fontRenderer.lineHeight / 2f));

      x += fontRenderer.width(line.substring(0, cursorCol));

      graphics.fill(
        x,
        y - 1,
        x + 1,
        y + fontRenderer.lineHeight,
        nameSignInfo.color().getTextColor() | 0xff000000
      );
    }
    ms.popPose();
  }
}
