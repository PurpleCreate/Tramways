package purplecreate.tramways.content.signs.demands;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import purplecreate.tramways.Tramways;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import purplecreate.tramways.content.signs.TramSignBlock;
import purplecreate.tramways.mixinInterfaces.ITemporarySpeedLimitTrain;

public class SpeedSignDemand extends SignDemand {
  @Override
  @Environment(EnvType.CLIENT)
  public ItemStack getIcon() {
    return AllBlocks.SPEEDOMETER.asStack();
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
    tag.putBoolean("Overridden", false);
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
  public void execute(CompoundTag tag, Train train, double distance) {
    if (train instanceof ITemporarySpeedLimitTrain tempSpeedLimitTrain) {
      if (tempSpeedLimitTrain.tempSpeedLimit$has()) {
        tag.putBoolean("Overridden", true);
        return;
      }
    }

    tag.putBoolean("Overridden", false);

    if (SignDemand.isManual(train))
      return;

    double nextThrottle = tag.getInt("Throttle") / 100d;

    double v = nextThrottle * train.maxSpeed(); // final velocity
    double u = Math.abs(train.speed); // initial velocity

    if (v >= u) {
      if (distance < 1)
        train.throttle = nextThrottle;
    } else {
      float a = train.acceleration(); // acceleration
      double s = ((v * v) - (u * u)) / (2 * a); // displacement (distance)

      if (distance <= -s)
        train.throttle = nextThrottle;
    }
  }

  @Override
  @Environment(EnvType.CLIENT)
  public void render(TramSignBlock.SignType signType, CompoundTag tag, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
    SignDemand.renderTextInCenter(tag.getInt("Throttle") + "", 0, .4f/16f, ms, buffer, light);

    if (tag.getBoolean("Overridden")) {
      ms.translate(0, 0, .001);
      SignDemand.renderTextInCenter("—", 0xdc2626, 1f/16f, ms, buffer, light);
    }
  }
}
