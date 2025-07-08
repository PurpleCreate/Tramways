package purplecreate.tramways.content.signs.demands;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.trains.entity.Train;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import purplecreate.tramways.TPartialModels;
import purplecreate.tramways.content.signs.TramSignBlock;

public class WhistleSignDemand extends SignDemand {
  @ExpectPlatform
  public static void sendWhistlePacket(Train train, boolean isHonk) {
    throw new AssertionError();
  }

  @Override
  @Environment(EnvType.CLIENT)
  public ItemStack getIcon() {
    return AllBlocks.STEAM_WHISTLE.asStack();
  }

  @Override
  @Environment(EnvType.CLIENT)
  public PartialModel getSignFace(TramSignBlock.SignType signType) {
    if (signType == TramSignBlock.SignType.RAILWAY) {
      return TPartialModels.GREY_RAILWAY_FACE;
    }

    return super.getSignFace(signType);
  }

  @Override
  public void execute(CompoundTag tag, Train train, double distance) {
    if (SignDemand.isManual(train))
      return;

    sendWhistlePacket(train, distance < 30 && distance > 0);
  }

  @Override
  @Environment(EnvType.CLIENT)
  public void render(TramSignBlock.SignType signType, CompoundTag tag, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
    SignDemand.renderTextInCenter("W", 0, .6f/16f, ms, buffer, light);
  }
}
