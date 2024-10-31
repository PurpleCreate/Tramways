package purplecreate.tramways.content.signs.network;

import purplecreate.tramways.content.signs.TramSignBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import purplecreate.tramways.util.C2SPacket;

public class SaveSignSettingsC2SPacket implements C2SPacket {
  final BlockPos pos;
  final ResourceLocation id;
  final CompoundTag demandExtra;

  public SaveSignSettingsC2SPacket(BlockPos pos, ResourceLocation id, CompoundTag demandExtra) {
    this.pos = pos;
    this.id = id;
    this.demandExtra = demandExtra;
  }

  public static SaveSignSettingsC2SPacket read(FriendlyByteBuf buffer) {
    BlockPos pos = buffer.readBlockPos();
    ResourceLocation id = buffer.readResourceLocation();
    CompoundTag demandExtra = buffer.readNbt();
    return new SaveSignSettingsC2SPacket(pos, id, demandExtra);
  }

  public void write(FriendlyByteBuf buffer) {
    buffer.writeBlockPos(pos);
    buffer.writeResourceLocation(id);
    buffer.writeNbt(demandExtra);
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
