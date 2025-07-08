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
import purplecreate.tramways.TPartialModels;
import purplecreate.tramways.Tramways;
import purplecreate.tramways.content.signs.TramSignBlock;

import java.util.List;

public class ArrowAuxSignDemand extends AuxSignDemand {
  @Override
  @Environment(EnvType.CLIENT)
  public ItemStack getIcon() {
    return new ItemStack(Items.OAK_SIGN);
  }

  @Override
  @Environment(EnvType.CLIENT)
  public void initSettingsGUI(ModularGuiLineBuilder builder) {
    builder.addSelectionScrollInput(0, 121, (ssi, l) -> {
      ssi.forOptions(List.of(
        Tramways.translatable("sign_demand.arrow_aux.both"),
        Tramways.translatable("sign_demand.arrow_aux.left"),
        Tramways.translatable("sign_demand.arrow_aux.right")
      ));
    }, "Arrows");
  }

  @Override
  public void setDefaultSettings(CompoundTag tag) {
    tag.putInt("Arrows", 0);
  }

  @Override
  public void validateSettings(CompoundTag received, CompoundTag disk) {
    int next = received.getInt("Arrows");

    if (next >= 0 && next <= 2) {
      disk.putInt("Arrows", next);
    } else {
      disk.putInt("Arrows", 0);
    }
  }

  @Override
  @Environment(EnvType.CLIENT)
  public PartialModel getSignFace(TramSignBlock.SignType signType) {
    return TPartialModels.TEXT_AUX_FACE;
  }

  @Override
  @Environment(EnvType.CLIENT)
  public void render(TramSignBlock.SignType signType, CompoundTag tag, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
    ms.pushPose();
    ms.translate(0, 4.6/16f, 0);

    int arrows = tag.getInt("Arrows");

    String text = "";
    if ((arrows >> 1) == 0) text += "←";
    if ((arrows & 1) == 0) text += "→";

    SignDemand.renderTextInCenter(text, 0, .6f/16f, ms, buffer, light);
    ms.popPose();
  }
}
