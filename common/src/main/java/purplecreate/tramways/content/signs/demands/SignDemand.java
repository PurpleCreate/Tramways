package purplecreate.tramways.content.signs.demands;

import com.jozufozu.flywheel.core.PartialModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import purplecreate.tramways.TPartialModels;
import purplecreate.tramways.content.signs.TramSignBlock;

import java.util.HashMap;
import java.util.Map;

public abstract class SignDemand {
  /**
   * INTERNAL USE ONLY!
   * @hidden
   */
  public static final Map<ResourceLocation, SignDemand> demands = new HashMap<>();

  public static final PartialModel TRAM_FACE = TPartialModels.TRAM_FACE;
  public static final PartialModel RED_RAILWAY_FACE = TPartialModels.RED_RAILWAY_FACE;
  public static final PartialModel GREY_RAILWAY_FACE = TPartialModels.GREY_RAILWAY_FACE;
  public static final PartialModel TSR_RAILWAY_FACE = TPartialModels.TSR_RAILWAY_FACE;

  /**
   * Use this to register a demand. You should probably use this
   * at the common setup event.
   *
   * @param location A unique id for your demand
   * @param demand A new instance of your demand
   */
  public static void register(ResourceLocation location, SignDemand demand) {
    demand.id = location;
    demands.put(location, demand);
  }

  /**
   * Check if a train is being driven manually
   *
   * @param train The train to check
   * @return True if the train is manually driven
   */
  public static boolean isManual(Train train) {
    return train.runtime.getSchedule() == null || train.runtime.paused;
  }

  /**
   * Render text in the center of the sign
   *
   * @param text The text to be rendered
   * @param color The color of the text as an int
   * @param scale The scale of the text (something like .4f/16f is good)
   * @param ms The PoseStack given by SignDemand#render
   * @param buffer The MultiBufferSource given by SignDemand#render
   * @param light The int light given by SignDemand#render
   */
  public static void renderTextInCenter(String text, int color, float scale, PoseStack ms, MultiBufferSource buffer, int light) {
    Font fontRenderer = Minecraft.getInstance().font;

    float x = fontRenderer.width(text) / -2f;
    float y = fontRenderer.lineHeight / -2f;

    ms.pushPose();

    ms.translate(.5, .5, 0);
    ms.scale(scale, scale, scale);
    ms.scale(1, -1, 1);
    ms.translate(.5, .5, 0);

    fontRenderer.drawInBatch(
      text,
      x,
      y,
      color,
      false,
      ms.last().pose(),
      buffer,
      Font.DisplayMode.NORMAL,
      0,
      light
    );

    ms.popPose();
  }

  // ---

  /**
   * INTERNAL USE ONLY!
   * @hidden
   */
  public ResourceLocation id;

  /**
   * @return true if the sign demand should be for auxiliary blocks
   */
  public boolean isAuxiliary() {
    return false;
  }

  /**
   * @return an icon to be shown in the settings menu
   */
  @Environment(EnvType.CLIENT)
  abstract public ItemStack getIcon();

  @Environment(EnvType.CLIENT)
  public PartialModel getSignFace(TramSignBlock.SignType signType) {
    if (signType == TramSignBlock.SignType.RAILWAY) {
      return RED_RAILWAY_FACE;
    } else {
      return TRAM_FACE;
    }
  }

  /**
   * Set up the settings menu for your demand
   *
   * @param builder Add elements using the builder
   */
  @Environment(EnvType.CLIENT)
  public void initSettingsGUI(ModularGuiLineBuilder builder) {}

  /**
   * Validate settings sent over the network
   *
   * @param received For reading settings from network
   * @param disk For writing validated settings
   */
  public void validateSettings(CompoundTag received, CompoundTag disk) {}

  /**
   * Set up the default settings for your demand
   *
   * @param tag Write the defaults to tag
   */
  public void setDefaultSettings(CompoundTag tag) {}

  /**
   * This function is ran every tick on the run-up to and whilst the train
   * runs over the track point
   *
   * @param tag The settings for your demand
   * @param train The train running past the sign
   */
  public void execute(CompoundTag tag, Train train, double distance) {}

  /**
   * <p>Use this to render text or symbols on the sign</p>
   *
   * <p>Tip: You can use <code>SignDemand.renderTextInCenter</code>
   * if you just need something simple</p>
   *
   * @param tag The settings for your demand
   */
  @Environment(EnvType.CLIENT)
  public void render(TramSignBlock.SignType signType, CompoundTag tag, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {}
}
