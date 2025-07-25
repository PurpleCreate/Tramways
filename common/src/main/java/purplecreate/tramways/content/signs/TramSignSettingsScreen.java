package purplecreate.tramways.content.signs;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import purplecreate.tramways.TNetworking;
import purplecreate.tramways.Tramways;
import purplecreate.tramways.content.signs.demands.SignDemand;
import purplecreate.tramways.content.signs.network.SaveSignSettingsC2SPacket;
import com.mojang.blaze3d.vertex.PoseStack;
import net.createmod.catnip.gui.AbstractSimiScreen;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.ModularGuiLine;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.createmod.catnip.gui.widget.AbstractSimiWidget;
import com.simibubi.create.foundation.gui.widget.Label;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class TramSignSettingsScreen extends AbstractSimiScreen {
  final AllGuiTextures background = AllGuiTextures.SCHEDULE_EDITOR;

  final List<ResourceLocation> availableDemands;

  final TramSignBlockEntity be;

  AbstractSimiWidget demandInput;
  Label demandInputLabel;
  ItemStack demandIcon;

  int nextDemand;
  final ModularGuiLine demandSettings = new ModularGuiLine();

  public TramSignSettingsScreen(TramSignBlockEntity be) {
    this.be = be;

    boolean isAuxiliary = be.getSignType() == TramSignBlock.SignType.AUXILIARY;
    availableDemands = SignDemand.demands.entrySet()
      .stream()
      .filter((entry) -> entry.getValue().isAuxiliary() == isAuxiliary)
      .map(Map.Entry::getKey)
      .toList();
  }

  private MutableComponent getDemandComponent(int i) {
    return getDemandComponent(availableDemands.get(i));
  }

  private MutableComponent getDemandComponent(ResourceLocation id) {
    if (id == null) return Component.empty();
    return Component.translatable(id.getNamespace() + ".sign_demand." + id.getPath());
  }

  @Override
  protected void init() {
    setWindowSize(background.getWidth(), background.getHeight());
    super.init();

    removeWidget(demandInput);
    demandSettings.forEach(this::removeWidget);

    ResourceLocation initialDemandId = be.getDemand() != null ? be.getDemand().id : null;
    if (initialDemandId != null) {
      int initialDemandIndex = availableDemands.indexOf(initialDemandId);
      demandInputLabel = new Label(guiLeft + 60, guiTop + 29, Component.empty()).withShadow();

      demandInput = new ScrollInput(guiLeft + 56, guiTop + 25, 143, 16)
        .withRange(0, availableDemands.size())
        .titled(Tramways.translatable("sign_demand"))
        .inverted()
        .calling(this::initDemandOptions)
        .setState(initialDemandIndex);

      initDemandOptions(initialDemandIndex);

      addRenderableWidget(demandInputLabel);
      addRenderableWidget(demandInput);
    }
  }

  private void initDemandOptions(int i) {
    nextDemand = i;

    demandSettings.forEach(this::removeWidget);
    demandSettings.clear();

    demandInputLabel.text = getDemandComponent(i);

    SignDemand demand = SignDemand.demands.get(availableDemands.get(i));
    demandIcon = demand.getIcon();
    demand.initSettingsGUI(new ModularGuiLineBuilder(font, demandSettings, guiLeft + 79, guiTop + 52));
    demandSettings.loadValues(be.getDemandExtra(), this::addRenderableWidget, this::addRenderableOnly);
  }

  @Override
  public void onClose() {
    super.onClose();

    CompoundTag tag = new CompoundTag();
    demandSettings.saveValues(tag);
    TNetworking.sendToServer(
      new SaveSignSettingsC2SPacket(be.getBlockPos(), availableDemands.get(nextDemand), tag)
    );
  }

  @Override
  protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
    background.render(graphics, guiLeft, guiTop);

    PoseStack ms = graphics.pose();
    ms.pushPose();
    ms.translate(0, guiTop + 47, 0);
    demandSettings.renderWidgetBG(guiLeft, graphics);
    ms.popPose();

    if (demandIcon != null) {
      GuiGameElement.of(demandIcon)
        .at(guiLeft + 56, guiTop + 48)
        .render(graphics);
    }
  }
}
