package purplecreate.tramways.content.announcements.config.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import de.mrjulsen.crn.client.gui.CreateDynamicWidgets;
import de.mrjulsen.crn.client.gui.CreateDynamicWidgets.ColorShade;
import de.mrjulsen.crn.client.gui.ModGuiIcons;
import de.mrjulsen.crn.client.gui.widgets.FlatIconButton;
import de.mrjulsen.crn.client.gui.widgets.create.CreateButton;
import de.mrjulsen.crn.client.gui.widgets.create.CreateItemPicker;
import de.mrjulsen.crn.client.gui.widgets.options.OptionEntry;
import de.mrjulsen.crn.client.gui.widgets.options.OptionsDataView;
import de.mrjulsen.crn.client.gui.widgets.options.OptionsView;
import de.mrjulsen.mcdragonlib.client.gui.events.DLGuiStandardEvents;
import de.mrjulsen.mcdragonlib.client.gui.widgets.base.DLGuiComponent;
import de.mrjulsen.mcdragonlib.client.gui.widgets.base.DLWindowManager;
import de.mrjulsen.mcdragonlib.client.gui.widgets.components.DLAbstractDataView.DataSlot;
import de.mrjulsen.mcdragonlib.client.gui.widgets.components.DLAbstractDataView.DataSlotComponent;
import de.mrjulsen.mcdragonlib.client.gui.widgets.components.DLAbstractDataView.SizeMode;
import de.mrjulsen.mcdragonlib.client.gui.widgets.components.DLButton;
import de.mrjulsen.mcdragonlib.client.gui.widgets.components.DLCycleButton;
import de.mrjulsen.mcdragonlib.client.gui.widgets.components.DLPanel;
import de.mrjulsen.mcdragonlib.client.gui.widgets.layout.FlowLayout;
import de.mrjulsen.mcdragonlib.client.gui.widgets.layout.TableLayout;
import de.mrjulsen.mcdragonlib.client.gui.widgets.layout.TableLayout.ColumnSizeMode;
import de.mrjulsen.mcdragonlib.client.render.GuiIcons;
import de.mrjulsen.mcdragonlib.client.util.DLGuiGraphics;
import de.mrjulsen.mcdragonlib.client.util.DLSprite;
import de.mrjulsen.mcdragonlib.client.util.DLTexture;
import de.mrjulsen.mcdragonlib.client.util.GuiUtils;
import de.mrjulsen.mcdragonlib.util.DLUtils;
import de.mrjulsen.mcdragonlib.util.math.Rectangle;
import net.createmod.catnip.gui.element.ScreenElement;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import purplecreate.tramways.Tramways;
import purplecreate.tramways.content.announcements.config.AnnouncementConfig;
import purplecreate.tramways.content.announcements.config.AnnouncementEvent;
import purplecreate.tramways.content.announcements.config.AnnouncementVariant;
import purplecreate.tramways.content.announcements.config.instructions.AnnouncementInstruction;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class SetupAnnouncementVariantsWindow extends AbstractAnnouncementsWindow {
  private final AnnouncementEvent event;
  private final OptionsView view;

  public SetupAnnouncementVariantsWindow(DLWindowManager manager, AnnouncementConfig config, Consumer<CompoundTag> onClose, AnnouncementEvent event) {
    super(manager, config, onClose);
    this.event = event;

    view = addComponent(new OptionsView(
      3,
      CreateDynamicWidgets.FooterSize.DEFAULT.size() + 1,
      GUI_WIDTH - 6,
      GUI_HEIGHT - (CreateDynamicWidgets.FooterSize.DEFAULT.size() + 1) - CreateDynamicWidgets.FooterSize.SMALL.size() - 1
    ));

    init();
  }

  private void init() {
    view.clearEntries();

    int i = 0;
    for (AnnouncementVariant variant : config.getVariants(event)) {
      OptionEntry<Integer> entry = view.addEntry(new OptionEntry<>(
        Tramways.translatable("announcements.variant_label", ++i),
        List.of(),
        (s, e) -> {
          ((OptionEntry)s).expanded.toggle();
          return false;
        }
      ));

      entry.dataView.itemBuilder.set((index) -> {
        OptionsDataView.DLBasicItem<Integer> item = new OptionsDataView.DLBasicItem<>(entry.dataView, index);

        if (index == -1) {
          item.subComponents.add(new DataSlotComponent("content", new AddButtonBar(true, () -> {
            variant.add(Tramways.rl("play_tts"), null);
            reloadInstructions(entry, variant);
          })));
        } else {
          item.subComponents.add(new DataSlotComponent("content", new InstructionConfig(entry, variant, index)));
        }
        return item;
      });
      entry.dataView.setWidth(entry.width() - 20 - 20);
      entry.dataView.dataSlots.add(new DataSlot("content", Component.empty(), 100, SizeMode.PERCENTAGE));
      reloadInstructions(entry, variant);

      TableLayout layout = new TableLayout();
      layout.addColumn("name", 1, ColumnSizeMode.PERCENTAGE);
      layout.addColumn("remove", 0, ColumnSizeMode.AUTO);
      layout.addColumn("dropdown", 20, ColumnSizeMode.FIXED);
      entry.getHeader().layout.set(layout);

      FlatIconButton removeButton = entry.getHeader().addComponent(
        new FlatIconButton(0, 0, ModGuiIcons.DELETE.getAsSprite(16, 16))
      );
      removeButton.layoutContraint.set("remove");
      removeButton.addEventListener(DLGuiStandardEvents.ClickEvent.class, (s, e) -> {
        config.removeVariant(event, variant);
        init();
        return false;
      });
    }

    view.addEntry(new AddButtonBar(false, () -> {
      config.addVariant(event);
      init();
    }));
  }

  private void reloadInstructions(OptionEntry<Integer> entry, AnnouncementVariant variant) {
    List<Integer> list = new ArrayList<>();
    for (int i = 0; i < variant.count(); i++) {
      list.add(i);
    }
    list.add(-1); // the add button
    entry.dataView.items.set(list);
  }

  private class InstructionConfig extends DLGuiComponent {
    private static final int PADDING = 2;
    private final DLGuiComponent dataPanel;
    private final DLGuiComponent arrowIcon;

    public InstructionConfig(OptionEntry<Integer> entry, AnnouncementVariant variant, int index) {
      super(0, 0, 0, 0);
      AnnouncementInstruction instruction = variant.get(index);

      CreateItemPicker<ResourceLocation> instructionType = addComponent(new CreateItemPicker<>(0, 0, 0));
      instructionType.formatter.set(id -> Component.translatable(id.getNamespace() + ".announcements.instruction." + id.getPath()));
      instructionType.items.addAll(AnnouncementInstruction.getAll());
      instructionType.selectedItem.set(Optional.ofNullable(instruction.getId()));
      instructionType.addEventListener(DLCycleButton.SelectedItemChanged.class, (s, e) -> {
        instructionType.selectedItem.get().ifPresent((id) -> variant.set(index, id, null));
        reloadInstructions(entry, variant);
        return false;
      });

      CreateButton deleteButton = addComponent(new CreateButton(0, 0, ModGuiIcons.DELETE.getAsCreateIcon()));
      deleteButton.addEventListener(DLGuiStandardEvents.ClickEvent.class, (s, e) -> {
        variant.remove(index);
        reloadInstructions(entry, variant);
        return false;
      });

      dataPanel = addComponent(new DLPanel(0, 20, 0, 18));
      variant.get(index).initConfigurationWidgets(dataPanel, variant.getData(index));
      dataPanel.addEventListener(DLGuiStandardEvents.ComponentPosAndSizeChanged.class, (s, e) -> {
        calculateHeight();
        return false;
      });

      arrowIcon = addComponent(new InstructionSeparator(0, 0));

      addEventListener(DLGuiStandardEvents.ComponentPosAndSizeChanged.class, (s, e) -> {
        if (!e.widthChanged()) return false;

        instructionType.setWidth(width() - 18 - 2);
        deleteButton.setX(width() - 18);
        dataPanel.setWidth(width());
        arrowIcon.setX((width() / 2.0) - 9);
        return false;
      });

      calculateHeight();
    }

    private void calculateHeight() {
      int h = 18 + PADDING + dataPanel.height() + PADDING + PADDING;
      arrowIcon.setY(h);
      h += 5 + PADDING;
      setHeight(h);
    }
  }

  private static class InstructionSeparator extends DLGuiComponent {
    public InstructionSeparator(int x, int y) {
      super(x, y, 16, 5);
    }

    @Override
    public void renderMainLayer(DLGuiGraphics graphics, double mouseX, double mouseY, Rectangle renderBounds) {
      DLSprite sprite = GuiIcons.ARROW_DOWN.getAsSprite(16, 16);
      sprite.render(
        graphics,
        this.width() / 2 - sprite.getWidth() / 2,
        this.height() / 2 - sprite.getHeight() / 2
      );
    }
  }

  private static class AddButtonBar extends DLGuiComponent {
    public AddButtonBar(boolean forInstructionConfig, Runnable onClick) {
      super(0, 0, 100, forInstructionConfig ? 18 : 26);

      layout.set(new FlowLayout());

      DLButton button = addComponent(getButton(forInstructionConfig, 0, height() / 2 - FlatIconButton.HEIGHT / 2, ModGuiIcons.ADD.getAsCreateIcon()));
      button.layoutContraint.set(FlowLayout.FlowConstraint.END);
      button.addEventListener(DLGuiStandardEvents.ClickEvent.class, (s, e) -> {
        onClick.run();
        return false;
      });

      if (forInstructionConfig) {
        addEventListener(DLGuiStandardEvents.ComponentPosAndSizeChanged.class, (s, e) -> {
          button.setWidth(width());
          return false;
        });
      }
    }

    private DLButton getButton(boolean forInstructionConfig, int x, int y, ScreenElement sprite) {
      if (forInstructionConfig) {
        DLTexture TEXTURE = new DLTexture(DLUtils.resourceLocation("create", "textures/gui/widgets.png"), 256, 256);
        return new CreateButton(x, y, sprite) {
          @Override
          public void renderMainLayer(DLGuiGraphics graphics, double mouseX, double mouseY, Rectangle renderBounds) {
            int u;
            int v = 0;
            if (!(Boolean)this.enabled.get()) {
              u = 36;
            } else if (this.isSelected()) {
              u = 18;
            } else {
              u = 0;
            }

            GuiUtils.drawTexture(TEXTURE, graphics, 0, 0, 1, 18, u, v);
            GuiUtils.drawTexture(TEXTURE, graphics, 1, 0, width() - 2, 18, u + 1, v, 16, 18, GuiUtils.TextureFillMode.TILE);
            GuiUtils.drawTexture(TEXTURE, graphics, width() - 1, 0, 1, 18, u + 17, v);

            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(true);
            this.icon.render(graphics.graphics(), width() / 2 - 8, 1);
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
          }
        };
      } else {
        return new FlatIconButton(x, y, sprite) {
          @Override
          public void renderMainLayer(DLGuiGraphics graphics, double mouseX, double mouseY, Rectangle renderBounds) {
            CreateDynamicWidgets.renderSingleShadeWidget(graphics, 0, 0, width(), height(), ColorShade.DARK);
            super.renderMainLayer(graphics, mouseX, mouseY, renderBounds);
          }
        };
      }
    }
  }
}
