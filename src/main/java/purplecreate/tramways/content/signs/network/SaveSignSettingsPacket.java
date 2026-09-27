package purplecreate.tramways.content.signs.network;

import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import purplecreate.tramways.TNetworking;
import purplecreate.tramways.content.signs.TramSignBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public record SaveSignSettingsPacket(BlockPos pos, ResourceLocation id, CompoundTag demandExtra) implements ServerboundPacketPayload {
  public static final StreamCodec<ByteBuf, SaveSignSettingsPacket> STREAM_CODEC = StreamCodec.composite(
    BlockPos.STREAM_CODEC, SaveSignSettingsPacket::pos,
    ResourceLocation.STREAM_CODEC, SaveSignSettingsPacket::id,
    ByteBufCodecs.COMPOUND_TAG, SaveSignSettingsPacket::demandExtra,
    SaveSignSettingsPacket::new
  );

  @Override
  public PacketTypeProvider getTypeProvider() {
    return TNetworking.S_SAVE_SIGN_SETTINGS;
  }

  public void handle(ServerPlayer sender) {
    if (sender == null) return;
    Level level = sender.level();
    if (!level.isLoaded(pos)) return;
    if (!pos.closerThan(sender.blockPosition(), 20)) return;

    if (level.getBlockEntity(pos) instanceof TramSignBlockEntity be) {
      be.setDemand(id);
      be.setDemandExtra(demandExtra);
    }
  }
}
