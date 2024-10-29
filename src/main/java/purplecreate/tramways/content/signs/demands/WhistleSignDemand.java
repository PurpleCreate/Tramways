package purplecreate.tramways.content.signs.demands;

import com.jozufozu.flywheel.core.PartialModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPackets;
import com.simibubi.create.content.trains.HonkPacket;
import com.simibubi.create.content.trains.entity.Train;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.PacketDistributor;
import purplecreate.tramways.content.signs.TramSignBlock;

public class WhistleSignDemand extends SignDemand {
  @Override
  @OnlyIn(Dist.CLIENT)
  public ItemStack getIcon() {
    return AllBlocks.STEAM_WHISTLE.asStack();
  }

  @Override
  public PartialModel getSignFace(TramSignBlock.SignType signType) {
    if (signType == TramSignBlock.SignType.RAILWAY) {
      return SignDemand.GREY_RAILWAY_FACE;
    }

    return super.getSignFace(signType);
  }

  @Override
  public void execute(CompoundTag tag, Train train, double distance) {
    if (SignDemand.isManual(train))
      return;

    AllPackets.getChannel().send(
      PacketDistributor.ALL.noArg(),
      new HonkPacket(train, distance < 30 && distance > 0)
    );
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void render(TramSignBlock.SignType signType, CompoundTag tag, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
    SignDemand.renderTextInCenter("W", 0, .6f/16f, ms, buffer, light);
  }
}
