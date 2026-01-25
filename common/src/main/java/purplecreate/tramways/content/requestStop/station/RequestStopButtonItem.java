package purplecreate.tramways.content.requestStop.station;

import com.simibubi.create.content.trains.station.StationBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import purplecreate.tramways.Tramways;

public class RequestStopButtonItem extends BlockItem {
  public RequestStopButtonItem(Block block, Properties properties) {
    super(block, properties);
  }

  @Override
  public InteractionResult useOn(UseOnContext context) {
    ItemStack stack = context.getItemInHand();
    Level level = context.getLevel();
    Player player = context.getPlayer();

    if (player == null)
      return InteractionResult.FAIL;

    if (player.isShiftKeyDown() && stack.has(DataComponents.BLOCK_ENTITY_DATA)) {
      if (level.isClientSide)
        return InteractionResult.SUCCESS;
      stack.remove(DataComponents.BLOCK_ENTITY_DATA);
      player.displayClientMessage(Tramways.translatable("request_stop.targeting.clear"), true);
      return InteractionResult.SUCCESS;
    }

    if (level.getBlockEntity(context.getClickedPos()) instanceof StationBlockEntity) {
      if (level.isClientSide)
        return InteractionResult.SUCCESS;
      CompoundTag tag = new CompoundTag();
      CompoundTag beTag = new CompoundTag();
      beTag.put("LinkedStation", NbtUtils.writeBlockPos(context.getClickedPos()));
      tag.put("BlockEntityTag", beTag);
      stack.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(tag));
      player.displayClientMessage(Tramways.translatable("request_stop.targeting.set"), true);
      return InteractionResult.SUCCESS;
    }

    if (!stack.has(DataComponents.BLOCK_ENTITY_DATA)) {
      player.displayClientMessage(
        Tramways.translatable("request_stop.targeting.no_tag")
          .withStyle(ChatFormatting.RED),
        true
      );
      return InteractionResult.FAIL;
    }

    InteractionResult result = super.useOn(context);
    if (level.isClientSide || result == InteractionResult.FAIL)
      return result;

    ItemStack itemInHand = player.getItemInHand(context.getHand());
    if (!itemInHand.isEmpty())
      itemInHand.remove(DataComponents.BLOCK_ENTITY_DATA);

    return result;
  }
}
