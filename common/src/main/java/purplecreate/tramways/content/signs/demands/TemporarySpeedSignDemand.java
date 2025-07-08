package purplecreate.tramways.content.signs.demands;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.trains.entity.Train;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import purplecreate.tramways.TPartialModels;
import purplecreate.tramways.content.signs.TramSignBlock;
import purplecreate.tramways.mixinInterfaces.ISpeedLimitableTrain;

public class TemporarySpeedSignDemand extends SpeedSignDemand {
  @Override
  @Environment(EnvType.CLIENT)
  public ItemStack getIcon() {
    return PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.SLOWNESS);
  }

  @Override
  @Environment(EnvType.CLIENT)
  public PartialModel getSignFace(TramSignBlock.SignType signType) {
    if (signType == TramSignBlock.SignType.RAILWAY) {
      return TPartialModels.TSR_RAILWAY_FACE;
    }

    return super.getSignFace(signType);
  }

  @Override
  public void execute(CompoundTag tag, Train train, double distance) {
    double nextThrottle = tag.getInt("Throttle") / 100d;

    // Ensure the throttle does not exceed the primary throttle
    if (train instanceof ISpeedLimitableTrain speedLimitableTrain) {
      double primaryLimit = speedLimitableTrain.primaryLimit$get();
      if (nextThrottle > primaryLimit) {
        nextThrottle = primaryLimit;
      }
    }

    double v = nextThrottle * train.maxSpeed(); // final velocity
    double u = Math.abs(train.speed); // initial velocity
    float a = train.acceleration(); // acceleration
    double s = ((v * v) - (u * u)) / (2 * a); // displacement (distance)

    if (
      distance <= -s
        && train instanceof ISpeedLimitableTrain speedLimitableTrain
    ) {
      speedLimitableTrain.tempSpeedLimit$set(
        nextThrottle,
        SignDemand.isManual(train)
      );
    }
  }

  @Override
  @Environment(EnvType.CLIENT)
  public void render(TramSignBlock.SignType signType, CompoundTag tag, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
    int fontColor = signType == TramSignBlock.SignType.RAILWAY
      ? 0
      : 0xdc2626;

    SignDemand.renderTextInCenter(tag.getInt("Throttle") + "", fontColor, .4f/16f, ms, buffer, light);
  }
}
