package purplecreate.tramways.content.announcements.config.gui;

import com.simibubi.create.foundation.gui.AllIcons;
import de.mrjulsen.crn.client.gui.CreateDynamicWidgets;
import de.mrjulsen.crn.client.gui.CreateDynamicWidgets.ColorShade;
import de.mrjulsen.crn.client.gui.CreateDynamicWidgets.FooterSize;
import de.mrjulsen.crn.client.gui.widgets.FlatIconButton;
import de.mrjulsen.crn.client.gui.widgets.create.CreateButton;
import de.mrjulsen.crn.client.gui.widgets.skins.ModernScrollbarComponentRenderer;
import de.mrjulsen.mcdragonlib.client.gui.events.DLGuiStandardEvents;
import de.mrjulsen.mcdragonlib.client.gui.widgets.base.DLGuiComponent;
import de.mrjulsen.mcdragonlib.client.gui.widgets.base.DLWindow;
import de.mrjulsen.mcdragonlib.client.gui.widgets.base.DLWindowManager;
import de.mrjulsen.mcdragonlib.client.gui.widgets.components.DLEditableLabel;
import de.mrjulsen.mcdragonlib.client.gui.widgets.components.DLPanel;
import de.mrjulsen.mcdragonlib.client.gui.widgets.components.DLScrollBar;
import de.mrjulsen.mcdragonlib.client.gui.widgets.components.DLTooltip;
import de.mrjulsen.mcdragonlib.client.gui.widgets.util.EAlign;
import de.mrjulsen.mcdragonlib.client.util.DLGuiGraphics;
import de.mrjulsen.mcdragonlib.client.util.GuiUtils;
import de.mrjulsen.mcdragonlib.data.ETextAlignment;
import de.mrjulsen.mcdragonlib.util.DLColor;
import de.mrjulsen.mcdragonlib.util.TextUtils;
import de.mrjulsen.mcdragonlib.util.math.Rectangle;
import de.mrjulsen.mcdragonlib.util.properties.Property;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import purplecreate.tramways.TNetworking;
import purplecreate.tramways.Tramways;
import purplecreate.tramways.config.TConfigs;
import purplecreate.tramways.content.announcements.engine.SoundEngine;
import purplecreate.tramways.content.announcements.engine.files.ClientFileManager;
import purplecreate.tramways.content.announcements.engine.files.ClientFileManager.UploadHistoryItem;
import purplecreate.tramways.content.announcements.engine.files.FileInfo;
import purplecreate.tramways.content.announcements.engine.files.TransmissionStatus;
import purplecreate.tramways.content.announcements.network.DeleteFileC2SPacket;
import purplecreate.tramways.content.announcements.network.UpdateFileNameC2SPacket;
import purplecreate.tramways.util.UploadDialog;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileManagerWindow extends AbstractTitledWindow {
  private final Minecraft mc = Minecraft.getInstance();
  private final ClientFileManager fm = ClientFileManager.getInstance();
  private final UUID currentPlayerId = mc.player.getGameProfile().getId();

  private final boolean adminMode;

  private final DLPanel contentPanel;
  private final DLScrollBar scrollbar;
  private FlatIconButton currentPreviewButton;

  private int lastUploadHistoryLength = 0;
  private final List<FileItem> uploadHistoryDisplays = new ArrayList<>();

  private int uploadHistoryLabelY;
  private int yourFilesLabelY;
  private int othersFilesLabelY;

  public FileManagerWindow(DLWindowManager manager, boolean adminMode) {
    super(manager, Tramways.translatable("announcements.file_manager" + (adminMode ? ".admin" : "") + ".title"));
    this.adminMode = adminMode;

    setSize(GUI_WIDTH, GUI_HEIGHT);
    manager.setPauseScreen(false);
    windowSpawnPosition.set(DLWindow.WindowPosition.CENTER);

    addComponent(new CreateButton(8, 223, AllIcons.I_CONFIG_BACK))
      .addEventListener(DLGuiStandardEvents.ClickEvent.class, (s, e) -> {
        getWindowManager().closeWindow(this);
        return false;
      });

    addComponent(new CreateButton(this.width() - 46, 223, AllIcons.I_REFRESH))
      .addEventListener(DLGuiStandardEvents.ClickEvent.class, (s, e) -> {
        fm.clearFinishedUploads();
        refresh();
        return false;
      });

    addComponent(new CreateButton(this.width() - 26, 223, AllIcons.I_ADD))
      .addEventListener(DLGuiStandardEvents.ClickEvent.class, (s, e) -> {
        startUpload();
        return false;
      });

    int cx = 3;
    int cy = FooterSize.DEFAULT.size() + 1;
    int cw = GUI_WIDTH - 6;
    int ch = GUI_HEIGHT - (FooterSize.DEFAULT.size() + 1) - FooterSize.SMALL.size() - 1;

    contentPanel = addComponent(new DLPanel(cx, cy, cw, ch) {
      @Override
      public void renderMainLayer(DLGuiGraphics graphics, double mouseX, double mouseY, Rectangle renderBounds) {
        super.renderMainLayer(graphics, mouseX, mouseY, renderBounds);
        renderSubtitle(graphics, Tramways.translatable("announcements.file_manager.upload_history"), uploadHistoryLabelY);
        renderSubtitle(graphics, Tramways.translatable("announcements.file_manager.your_files"), yourFilesLabelY);
        renderSubtitle(graphics, Tramways.translatable("announcements.file_manager.others_files"), othersFilesLabelY);
      }
    });
    contentPanel.anchor.set2(EAlign.values());
    contentPanel.inputConsumptionPolicy.set((type) -> false);

    scrollbar = addComponent(new DLScrollBar(cx + cw - 5, cy, 5, ch, DLScrollBar.Orientation.VERTICAL));
    scrollbar.componentRenderer.set(ModernScrollbarComponentRenderer.INSTANCE);
    scrollbar.anchor.set2(EAlign.TOP, EAlign.BOTTOM, EAlign.RIGHT);
    scrollbar.scrollerSize.set(0);
    scrollbar.screenSize.set(contentPanel.height());
    scrollbar.scrollSteps.set(15);
    scrollbar.max.set(0);
    scrollbar.inputConsumptionPolicy.set((type) -> true);
    scrollbar.addEventListener(DLScrollBar.ValueChangedEvent.class, (s, e) -> {
      contentPanel.setScrollOffsetY(e.value());
      return false;
    });

    addEventListener(DLGuiStandardEvents.ScrollEvent.class, scrollbar::invokeEvent);

    fm.clearFinishedUploads();
    refresh();
  }

  public static boolean forceAdminMode() {
    return TConfigs.client().alwaysUseAdminMode.get() && Minecraft.getInstance().player.hasPermissions(2);
  }

  private void refresh() {
    int x = 10;
    int y = 10;
    int w = contentPanel.width() - 20;

    contentPanel.clearComponents();
    uploadHistoryDisplays.clear();

    List<FileInfo> yourFiles = new ArrayList<>();
    List<FileInfo> othersFiles = new ArrayList<>();

    for (FileInfo toInsert : fm.getFiles()) {
      List<FileInfo> list = toInsert.ownerId().equals(currentPlayerId)
        ? yourFiles
        : othersFiles;

      int min = 0;
      int max = list.size();

      while (max > min) {
        int test = (min + max) / 2;
        if (list.get(test).name().compareToIgnoreCase(toInsert.name()) < 0) {
          min = test + 1;
        } else {
          max = test;
        }
      }

      list.add(min, toInsert);
    }

    if (!fm.getUploadHistory().isEmpty()) {
      uploadHistoryLabelY = y;
      y += 9 + 2;
      for (UploadHistoryItem item : fm.getUploadHistory()) {
        FileItem disp = new FileItem(x, y, w, item.info());
        y += contentPanel.addComponent(disp).height() + 2;
        uploadHistoryDisplays.add(disp);
      }
      y += 4;
      populateUploadHistory();
    } else {
      uploadHistoryLabelY = -1;
    }

    if (!yourFiles.isEmpty()) {
      yourFilesLabelY = y;
      y += 9 + 2;
      for (FileInfo info : yourFiles) {
        y += contentPanel.addComponent(new FileItem(x, y, w, info)).height() + 2;
      }
      y += 4;
    } else {
      yourFilesLabelY = -1;
    }

    if (!othersFiles.isEmpty()) {
      othersFilesLabelY = y;
      y += 9 + 2;
      for (FileInfo info : othersFiles) {
        y += contentPanel.addComponent(new FileItem(x, y, w, info)).height() + 2;
      }
      y += 4;
    } else {
      othersFilesLabelY = -1;
    }

    y += 6;
    scrollbar.max.set(y);
    scrollbar.screenSize.set(contentPanel.height());
  }

  private void populateUploadHistory() {
    for (int i = 0; i < uploadHistoryDisplays.size(); i++) {
      FileItem disp = uploadHistoryDisplays.get(i);
      UploadHistoryItem item = fm.getUploadHistory().get(i);
      disp.status.set(item.status());
      disp.details.set(item.details());
    }
  }

  private void renderSubtitle(DLGuiGraphics graphics, Component text, int y) {
    if (y == -1) return;

    y -= (int)contentPanel.getScrollOffsetY();

    Font font = graphics.defaultFont();
    int lx = 24 + font.width(text);

    GuiUtils.fill(graphics, 10, y + 4, 6, 1, DLColor.WHITE);
    GuiUtils.drawString(graphics, font, 20, y, text, DLColor.WHITE, ETextAlignment.LEFT, true);
    GuiUtils.fill(graphics, lx, y + 4, contentPanel.width() - lx - 10, 1, DLColor.WHITE);
  }

  private void setCurrentPreviewButton(FlatIconButton button) {
    if (currentPreviewButton != null) {
      currentPreviewButton.icon.set(AllIcons.I_PLAY);
    }

    if (button != null) {
      button.icon.set(AllIcons.I_STOP);
    }

    currentPreviewButton = button;
  }

  private void startUpload() {
    new Thread(() -> {
      String[] files = new UploadDialog()
        .title("Upload Files")
        .patterns(FileInfo.Type.getPatterns())
        .patternDescription("Audio Files")
        .allowMultiple()
        .createSync();

      for (String name : files) {
        File file = new File(name);
        InputStream stream;
        try {
          stream = new BufferedInputStream(new FileInputStream(file));
        } catch (IOException e) {
          fm.reportClientUploadError(FileInfo.fileNameOnly(file.getName()), Tramways.translatable("announcements.file_manager.details.client_read_error"));
          return;
        }

        try {
          fm.startTransmitting(null, FileInfo.determine(file, stream), stream, adminMode);
        } catch (FileInfo.DeterminationException e) {
          fm.reportClientUploadError(e.partialInfo, e.details);
        }
      }
    }).start();
  }

  @Override
  public void tick() {
    super.tick();

    if (SoundEngine.isPreviewStopped() && currentPreviewButton != null) {
      setCurrentPreviewButton(null);
    }

    if (lastUploadHistoryLength != fm.getUploadHistory().size()) {
      lastUploadHistoryLength = fm.getUploadHistory().size();
      refresh();
    } else {
      populateUploadHistory();
    }
  }

  @Override
  public void close() throws Exception {
    super.close();
    SoundEngine.stopPreview();
  }

  public class FileItem extends DLGuiComponent {
    private final FileInfo info;
    public final Property<TransmissionStatus> status = new Property<>(null);
    public final Property<Component> details = new Property<>(null);
    public final Property<Boolean> markedDeleted = new Property<>(false);

    private boolean showButtons;
    private boolean isOwner;
    private DLEditableLabel nameBox = null;

    public FileItem(int x, int y, int w, FileInfo info) {
      super(x, y, w, 20);
      this.info = info;

      for (Property<?> p : new Property[]{status, details, markedDeleted}) {
        p.withAfterPropertyChangedCallback((v1, v2) -> {
          refresh();
        });
      }

      refresh();
    }

    private void refreshTooltip() {
      List<Component> tooltip = new ArrayList<>();
      TransmissionStatus status = this.status.get();

      tooltip.add(Component.literal(info.name()));

      if (status == null) {
        tooltip.add(Tramways.translatable("announcements.file_manager.uploaded_by", info.owner()).withStyle(ChatFormatting.GRAY));
      } else {
        tooltip.add(status.translatable(details.get()).withStyle(ChatFormatting.GRAY));
      }

      this.tooltip.set(new DLTooltip(tooltip, 200));
      if (nameBox != null) {
        nameBox.tooltip.set(new DLTooltip(tooltip, 200));
      }
    }

    private void refresh() {
      clearComponents();

      int cx = width();
      int cy = 1;
      showButtons = status.get() == null && !markedDeleted.get();
      isOwner = adminMode || info.ownerId().equals(currentPlayerId);

      if (showButtons && isOwner) {
        cx -= 18 + 1;
        FlatIconButton deleteButton = addComponent(new FlatIconButton(cx, cy, AllIcons.I_TRASH));
        deleteButton.addEventListener(DLGuiStandardEvents.ClickEvent.class, (s, e) -> {
          TNetworking.sendToServer(new DeleteFileC2SPacket(info));
          markedDeleted.set(true);
          return false;
        });
      }

      if (showButtons) {
        cx -= 18 + 1;
        addComponent(new FlatIconButton(cx, cy, AllIcons.I_PLAY) {
          @Override
          public boolean defaultButtonClickAction(DLGuiComponent src, DLGuiStandardEvents.ClickEvent event) {
            if (currentPreviewButton == this) {
              SoundEngine.stopPreview();
              return false;
            }

            try {
              InputStream stream = new FileInputStream(info.getRealLocation(ClientFileManager.getInstance().getFolder()));
              SoundEngine.playPreview(info.fileType().createAudioStream(stream));
              setCurrentPreviewButton(this);
            } catch (IOException ex) {
              Tramways.LOGGER.warn("An exception occurred whilst previewing {}", info.name(), ex);
            }
            return false;
          }
        });
      }

      if (showButtons && isOwner) {
        nameBox = addComponent(new DLEditableLabel(6, 2, 140, 16));
        nameBox.text.set(info.name());
        nameBox.addEventListener(DLEditableLabel.TextEditedEvent.class, (s, e) -> {
          TNetworking.sendToServer(new UpdateFileNameC2SPacket(info, e.text()));
          return false;
        });
      }

      refreshTooltip();
    }

    @Override
    public void renderMainLayer(DLGuiGraphics graphics, double mouseX, double mouseY, Rectangle renderBounds) {
      Font font = graphics.defaultFont();

      CreateDynamicWidgets.renderSingleShadeWidget(graphics, 0, 0, width(), height(), ColorShade.DARK);

      if (!showButtons || !isOwner) {
        GuiUtils.drawString(
          graphics,
          font,
          6,
          height() / 2 - font.lineHeight / 2,
          markedDeleted.get()
            ? Tramways.translatable("announcements.file_manager.file_deleted")
            : TextUtils.truncateWithEllipsis(graphics.defaultFont(), Component.literal(info.name()), 140),
          markedDeleted.get()
            ? DLColor.RED
            : DLColor.WHITE,
          ETextAlignment.LEFT,
          false
        );
      }

      TransmissionStatus status = this.status.get();
      if (status != null) {
        GuiUtils.drawString(
          graphics,
          font,
          width() - 6,
          height() / 2 - font.lineHeight / 2,
          status == TransmissionStatus.PROGRESSING
            ? details.get()
            : status.translatable(),
          DLColor.WHITE,
          ETextAlignment.RIGHT,
          false
        );
      }
    }
  }
}
