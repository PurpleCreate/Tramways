package purplecreate.tramways.content.signs.demands;

import com.simibubi.create.content.trains.HonkPacket;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.trains.entity.Train;
import net.createmod.catnip.platform.CatnipServices;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import purplecreate.tramways.TPartialModels;
import purplecreate.tramways.content.signs.TramSignBlock;

public class WhistleSignDemand extends SignDemand {
  public static void sendWhistlePacket(Train train, boolean isHonk) {
    CatnipServices.NETWORK.sendToAllClients(new HonkPacket.Clientbound(train, isHonk));
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
  public Result execute(CompoundTag tag, Train train, double distance) {
    if (!SignDemand.isManual(train))
      sendWhistlePacket(train, distance < 30 && distance > 0);

    return null;
  }

  @Override
  @Environment(EnvType.CLIENT)
  public void render(TramSignBlock.SignType signType, CompoundTag tag, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
    SignDemand.renderTextInCenter("W", 0, .6f/16f, ms, buffer, light);
  }
}
