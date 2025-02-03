package purplecreate.tramways.content.stationDeco.nameSign.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import purplecreate.tramways.content.stationDeco.nameSign.NameSignBlockEntity;
import purplecreate.tramways.util.C2SPacket;

import java.util.ArrayList;
import java.util.List;

public class UpdateNameSignC2SPacket implements C2SPacket {
  BlockPos pos;
  List<String> lines;

  public UpdateNameSignC2SPacket(BlockPos pos, List<String> lines) {
    this.pos = pos;
    this.lines = lines;
  }

  public static UpdateNameSignC2SPacket read(FriendlyByteBuf buf) {
    BlockPos pos = buf.readBlockPos();

    int length = buf.readVarInt();
    List<String> lines = new ArrayList<>();
    for (int i = 0; i < length; i++)
      lines.add(buf.readUtf());

    return new UpdateNameSignC2SPacket(pos, lines);
  }

  @Override
  public void write(FriendlyByteBuf buf) {
    buf.writeBlockPos(pos);
    buf.writeVarInt(lines.size());
    for (String line : lines)
      buf.writeUtf(line);
  }

  @Override
  public void handle(ServerPlayer player) {
    if (player == null) return;
    Level level = player.level();
    if (!level.isLoaded(pos)) return;
    if (!pos.closerThan(player.blockPosition(), 20)) return;

    if (level.getBlockEntity(pos) instanceof NameSignBlockEntity be) {
      be.lines = lines;
      be.notifyUpdate();
    }
  }
}
