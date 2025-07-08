package purplecreate.tramways.content.signs.demands;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import purplecreate.tramways.TPartialModels;
import purplecreate.tramways.Tramways;
import purplecreate.tramways.content.signs.TramSignBlock;

public class AdvanceWarningAuxSignDemand extends AuxSignDemand {
  @Override
  @Environment(EnvType.CLIENT)
  public ItemStack getIcon() {
    return PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.SLOWNESS);
  }

  @Override
  @Environment(EnvType.CLIENT)
  public PartialModel getSignFace(TramSignBlock.SignType signType) {
    return TPartialModels.ADVANCE_WARNING_AUX_FACE;
  }

  @Override
  @Environment(EnvType.CLIENT)
  public void initSettingsGUI(ModularGuiLineBuilder builder) {
    builder.addScrollInput(0, 34, (si, l) -> {
      si.titled(Tramways.translatable("sign_demand.speed"))
        .withRange(1, 101);
      l.withSuffix("%");
    }, "Throttle");
  }

  @Override
  public void setDefaultSettings(CompoundTag tag) {
    tag.putInt("Throttle", 100);
  }

  @Override
  public void validateSettings(CompoundTag received, CompoundTag disk) {
    int nextThrottle = received.getInt("Throttle");

    if (nextThrottle >= 1 && nextThrottle <= 100) {
      disk.putInt("Throttle", nextThrottle);
    } else {
      disk.putInt("Throttle", 100);
    }
  }

  @Override
  @Environment(EnvType.CLIENT)
  public void render(TramSignBlock.SignType signType, CompoundTag tag, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
    ms.pushPose();
    ms.translate(0, 2/16f, 0);
    SignDemand.renderTextInCenter(tag.getInt("Throttle") + "", 0, .4f/16f, ms, buffer, light);
    ms.popPose();
  }
}
