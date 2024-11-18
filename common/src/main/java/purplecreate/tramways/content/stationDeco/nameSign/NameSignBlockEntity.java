package purplecreate.tramways.content.stationDeco.nameSign;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import purplecreate.tramways.TTags;

import java.util.List;

public class NameSignBlockEntity extends SmartBlockEntity {
  public BlockState wood = Blocks.OAK_PLANKS.defaultBlockState();
  public String text = "";

  public NameSignBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
  }

  @Override
  public void addBehaviours(List<BlockEntityBehaviour> list) {}

  public InteractionResult applyItem(ItemStack stack) {
    if (stack.getItem() instanceof BlockItem blockItem) {
      BlockState wood = blockItem.getBlock().defaultBlockState();
      if (wood == this.wood)
        return InteractionResult.PASS;
      if (!wood.is(TTags.NAME_SIGN_INNER))
        return InteractionResult.PASS;
      if (level.isClientSide())
        return InteractionResult.SUCCESS;
      this.wood = wood;
      notifyUpdate();
      return InteractionResult.SUCCESS;
    }
    return InteractionResult.PASS;
  }

  @Override
  protected void read(CompoundTag tag, boolean clientPacket) {
    super.read(tag, clientPacket);
    wood = NbtUtils.readBlockState(blockHolderGetter(), tag.getCompound("Wood"));
    text = tag.getString("Text");
  }

  @Override
  protected void write(CompoundTag tag, boolean clientPacket) {
    super.write(tag, clientPacket);
    tag.put("Wood", NbtUtils.writeBlockState(wood));
    tag.putString("Text", text);
  }
}
