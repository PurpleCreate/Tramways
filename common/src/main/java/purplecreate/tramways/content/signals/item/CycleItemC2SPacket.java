package purplecreate.tramways.content.signals.item;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import purplecreate.tramways.util.C2SPacket;

public class CycleItemC2SPacket implements C2SPacket {
  Item targetItem;

  public CycleItemC2SPacket(Item targetItem) {
    this.targetItem = targetItem;
  }

  public static CycleItemC2SPacket read(FriendlyByteBuf buf) {
    return new CycleItemC2SPacket(BuiltInRegistries.ITEM.get(buf.readResourceLocation()));
  }

  @Override
  public void write(FriendlyByteBuf buf) {
    buf.writeResourceLocation(BuiltInRegistries.ITEM.getKey(targetItem));
  }

  @Override
  public void handle(ServerPlayer player) {
    boolean success = false;

    for (InteractionHand hand : InteractionHand.values()) {
      ItemStack heldStack = player.getItemInHand(hand);
      if (CycleRegistries.compareRegistry(heldStack.getItem(), targetItem)) {
        player.setItemInHand(hand, new ItemStack(targetItem, heldStack.getCount()));
        success = true;
        break;
      }
    }

    if (!success) {
      player.connection.disconnect(Component.literal("Invalid target item"));
    }
  }
}
