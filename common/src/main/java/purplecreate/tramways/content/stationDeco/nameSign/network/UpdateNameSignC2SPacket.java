package purplecreate.tramways.content.stationDeco.nameSign.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import purplecreate.tramways.content.stationDeco.nameSign.NameSignBlockEntity;
import purplecreate.tramways.util.C2SPacket;

public class UpdateNameSignC2SPacket implements C2SPacket {
  BlockPos pos;
  String text;

  public UpdateNameSignC2SPacket(BlockPos pos, String text) {
    this.pos = pos;
    this.text = text;
  }

  public static UpdateNameSignC2SPacket read(FriendlyByteBuf buf) {
    BlockPos pos = buf.readBlockPos();
    String text = buf.readUtf();
    return new UpdateNameSignC2SPacket(pos, text);
  }

  @Override
  public void write(FriendlyByteBuf buf) {
    buf.writeBlockPos(pos);
    buf.writeUtf(text);
  }

  @Override
  public void handle(ServerPlayer player) {
    if (player == null) return;
    Level level = player.level();
    if (!level.isLoaded(pos)) return;
    if (!pos.closerThan(player.blockPosition(), 20)) return;

    if (level.getBlockEntity(pos) instanceof NameSignBlockEntity be) {
      be.text = text;
      be.notifyUpdate();
    }
  }
}
