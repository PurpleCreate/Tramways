package purplecreate.tramways.content.signals.routing;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.content.trains.signal.SignalBlock;
import com.simibubi.create.content.trains.signal.SignalBlockEntity;
import com.simibubi.create.content.trains.signal.SignalBoundary;
import net.createmod.catnip.gui.ScreenOpener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import purplecreate.tramways.TNetworking;
import purplecreate.tramways.Tramways;

public class RouteConfiguratorItem extends Item {
  public RouteConfiguratorItem(Properties properties) {
    super(properties);
  }

  @Override
  public InteractionResult useOn(UseOnContext context) {
    Player player = context.getPlayer();
    Level level = context.getLevel();
    BlockPos pos = context.getClickedPos();
    SignalBlockEntity be = level.getBlockEntity(pos, AllBlockEntityTypes.TRACK_SIGNAL.get()).orElse(null);

    if (player == null || be == null) return InteractionResult.PASS;
    if (!level.isClientSide) return InteractionResult.SUCCESS;

    useOnClient(player, pos, be);
    return InteractionResult.SUCCESS;
  }

  @Environment(EnvType.CLIENT)
  private void useOnClient(Player player, BlockPos pos, SignalBlockEntity be) {
    SignalBoundary boundary = be.getSignal();
    SignalBlock.SignalType type = be.getBlockState().getValue(SignalBlock.TYPE);

    if (boundary == null) {
      player.displayClientMessage(Tramways.translatable("route_configurator.used_on_invalid").withStyle(ChatFormatting.RED), true);
      return;
    } else if (type != SignalBlock.SignalType.ENTRY_SIGNAL) {
      player.displayClientMessage(Tramways.translatable("route_configurator.used_on_brass").withStyle(ChatFormatting.RED), true);
      return;
    }

    if (player.isShiftKeyDown()) {
      TNetworking.sendToServer(UpdateSignalC2SPacket.removeRoute(pos));
      player.displayClientMessage(Tramways.translatable("route_configurator.remove_config"), true);
    } else {
      ScreenOpener.open(new RouteConfiguratorScreen(be));
    }
  }
}
