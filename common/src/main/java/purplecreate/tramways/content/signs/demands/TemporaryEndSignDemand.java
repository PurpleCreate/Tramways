package purplecreate.tramways.content.signs.demands;

import com.jozufozu.flywheel.core.PartialModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.trains.entity.Train;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import purplecreate.tramways.content.signs.TramSignBlock;
import purplecreate.tramways.mixinInterfaces.ISpeedLimitableTrain;

public class TemporaryEndSignDemand extends SignDemand {
  @Override
  @Environment(EnvType.CLIENT)
  public ItemStack getIcon() {
    return new ItemStack(Items.MILK_BUCKET);
  }

  @Override
  @Environment(EnvType.CLIENT)
  public PartialModel getSignFace(TramSignBlock.SignType signType) {
    if (signType == TramSignBlock.SignType.RAILWAY) {
      return SignDemand.TSR_RAILWAY_FACE;
    }

    return super.getSignFace(signType);
  }

  @Override
  public void execute(CompoundTag tag, Train train, double distance) {
    if (
      distance < 1
        && train instanceof ISpeedLimitableTrain speedLimitableTrain
    ) {
      speedLimitableTrain.tempSpeedLimit$restore(SignDemand.isManual(train));
    }
  }

  @Override
  @Environment(EnvType.CLIENT)
  public void render(TramSignBlock.SignType signType, CompoundTag tag, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
    int fontColor = signType == TramSignBlock.SignType.RAILWAY
      ? 0
      : 0xdc2626;

    SignDemand.renderTextInCenter("T", fontColor, .4f/16f, ms, buffer, light);
  }
}
