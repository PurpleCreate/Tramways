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
import net.minecraft.locale.Language;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.block.Block;
import purplecreate.tramways.TBlocks;
import purplecreate.tramways.TNetworking;
import purplecreate.tramways.Tramways;
import purplecreate.tramways.content.stationDeco.nameSign.info.NameSignInfo;
import purplecreate.tramways.content.stationDeco.nameSign.network.UpdateNameSignC2SPacket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

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

  private String text;
  private TextFieldHelper field;

  public NameSignScreen(NameSignBlockEntity be) {
    super(Component.translatable("sign.edit"));

    Block block = be.getBlockState().getBlock();
    this.be = be;
    this.text = be.text;
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
      () -> this.text,
      (text) -> {
        this.text = text;
        TNetworking.sendToServer(new UpdateNameSignC2SPacket(be.getBlockPos(), text));
      },
      TextFieldHelper.createClipboardGetter(minecraft),
      TextFieldHelper.createClipboardSetter(minecraft),
      (text) -> {
        FormattedText formattedText = Component.literal(this.text);
        List<FormattedCharSequence> lines = minecraft.font.split(formattedText, nameSignInfo.width());
        boolean valid = lines.size() <= 4;

        if (valid && lines.size() == 4)
          valid = minecraft.font.width(lines.get(3)) <= nameSignInfo.width();

        return valid;
      }
    );
  }

  @Override
  public void tick() {
    ticks++;
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    if (keyCode != 256)
      return field.keyPressed(keyCode);
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
    List<FormattedText> lines = new ArrayList<>();
    int cursorPos = field.getCursorPos();
    AtomicInteger cursorRow = new AtomicInteger();
    AtomicInteger cursorCol = new AtomicInteger();
    AtomicBoolean foundCursorPos = new AtomicBoolean(false);
    boolean showCursor = ticks / 6 % 2 != 0;

    fontRenderer.getSplitter().splitLines(
      this.text,
      nameSignInfo.width(),
      Style.EMPTY,
      false,
      (style, from, to) -> {
        String line = this.text.substring(from, to);

        if (cursorPos >= from && cursorPos <= to) {
          cursorCol.set(cursorPos - from);
          foundCursorPos.set(true);
        } else if (!foundCursorPos.get()) {
          cursorRow.incrementAndGet();
        }

        lines.add(FormattedText.of(line, style));
      }
    );

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
      Language.getInstance().getVisualOrder(lines),
      fontRenderer,
      ms,
      graphics.bufferSource(),
      LightTexture.FULL_BRIGHT,
      false
    );

    // cursor
    if (showCursor) {
      FormattedText line = lines.isEmpty()
        ? FormattedText.of("")
        : lines.get(cursorRow.get());

      int x = switch (nameSignInfo.align()) {
        case LEFT -> nameSignInfo.offset();
        case RIGHT -> -nameSignInfo.offset();
        case CENTER -> (fontRenderer.width(line) / -2) + nameSignInfo.offset();
      };
      int y = (int) (cursorRow.get() * fontRenderer.lineHeight - (lines.size() * fontRenderer.lineHeight / 2f));

      x += fontRenderer.width(
        line
          .getString()
          .substring(0, cursorCol.get())
      );

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
