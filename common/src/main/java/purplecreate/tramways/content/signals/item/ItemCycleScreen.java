package purplecreate.tramways.content.signals.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.Label;
import com.simibubi.create.foundation.gui.widget.SelectionScrollInput;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import purplecreate.tramways.TGuiSprites;
import purplecreate.tramways.TNetworking;
import purplecreate.tramways.Tramways;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ItemCycleScreen extends AbstractSimiScreen {
  private static final TGuiSprites bg = TGuiSprites.ITEM_CYCLE;

  private int initialCategoryIndex = 0;
  private int initialItemIndex = 0;

  private final List<String> categories = new ArrayList<>();
  private final List<List<ItemStack>> items = new ArrayList<>();

  private SelectionScrollInput itemBox;
  private SelectionScrollInput categoryBox;

  public ItemCycleScreen(CyclableItem initial, Map<String, Set<CyclableItem>> entries) {
    for (Map.Entry<String, Set<CyclableItem>> entry : entries.entrySet()) {
      List<ItemStack> itemStackList = new ArrayList<>();

      for (CyclableItem item : entry.getValue()) {
        if (initial == item) {
          initialCategoryIndex = categories.size();
          initialItemIndex = itemStackList.size();
        }

        itemStackList.add(new ItemStack(item));
      }

      categories.add(entry.getKey());
      items.add(itemStackList);
    }
  }

  private ItemStack getItem() {
    return items.get(categoryBox.getState()).get(itemBox.getState());
  }

  @Override
  protected void init() {
    setWindowSize(bg.getWidth(), bg.getHeight());
    super.init();

    addRenderableWidget(
      new IconButton(guiLeft + 228, guiTop + 79, AllIcons.I_CONFIRM)
        .withCallback(this::onClose)
    );

    Label categoryLabel = new Label(guiLeft + 43, guiTop + 29, Component.empty()).withShadow();
    Label itemLabel = new Label(guiLeft + 43, guiTop + 51, Component.empty()).withShadow();

    categoryBox = (SelectionScrollInput) new SelectionScrollInput(guiLeft + 39, guiTop + 25, 126, 16)
      .forOptions(categories.stream().map(k -> Tramways.translatable("cycle_category." + k)).toList())
      .calling(this::initItemBox)
      .writingTo(categoryLabel)
      .setState(initialCategoryIndex);

    itemBox = (SelectionScrollInput) new SelectionScrollInput(guiLeft + 39, guiTop + 47, 126, 16)
      .writingTo(itemLabel);

    initItemBox(initialCategoryIndex, true);
    addRenderableWidgets(categoryLabel, itemLabel, categoryBox, itemBox);
  }

  protected void initItemBox(int i, boolean initial) {
    itemBox
      .forOptions(items.get(i).stream().map(ItemStack::getHoverName).toList())
      .setState(initial ? initialItemIndex : 0);
  }

  protected void initItemBox(int i) {
    initItemBox(i, false);
  }

  @Override
  protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
    bg.blit(graphics, guiLeft, guiTop);
    renderBlock(graphics);
  }

  protected void renderBlock(GuiGraphics graphics) {
    PoseStack ms = graphics.pose();
    ItemStack stack = getItem();

    int x = guiLeft + 182;
    int y = guiTop + 28;
    int z = 2;

    ms.pushPose();
    ms.translate(x, y, 0);
    ms.scale(z, z, z);
    graphics.renderItem(stack, 0, 0);
    ms.popPose();
  }

  @Override
  public void onClose() {
    super.onClose();
    TNetworking.sendToServer(new CycleItemC2SPacket(getItem().getItem()));
  }
}
