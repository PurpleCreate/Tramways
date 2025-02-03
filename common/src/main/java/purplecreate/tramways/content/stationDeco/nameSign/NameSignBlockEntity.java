package purplecreate.tramways.content.stationDeco.nameSign;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import purplecreate.tramways.TTags;

import java.util.ArrayList;
import java.util.List;

public class NameSignBlockEntity extends SmartBlockEntity {
  public BlockState wood = Blocks.OAK_PLANKS.defaultBlockState();
  public List<String> lines = List.of("", "", "", "");
  public int textWidth = 60;

  public NameSignBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
  }

  @Override
  public void addBehaviours(List<BlockEntityBehaviour> list) {}

  public List<String> getLinesSafe() {
    List<String> safe = new ArrayList<>(lines);
    for (int i = 0; i < 4 - safe.size(); i++)
      safe.add("");
    return safe.subList(0, 4);
  }

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

    if (!tag.contains("Text")) {
      List<String> loadedLines = new ArrayList<>();
      for (int i = 0; i < 4; i++)
        loadedLines.add(tag.getString("Line" + i));
      lines = loadedLines;
    } else {
      // legacy
      Font fontRenderer = Minecraft.getInstance().font;
      lines = fontRenderer.getSplitter().splitLines(tag.getString("Text"), textWidth, Style.EMPTY)
        .stream()
        .map(FormattedText::getString)
        .toList();
    }
  }

  @Override
  protected void write(CompoundTag tag, boolean clientPacket) {
    super.write(tag, clientPacket);
    tag.put("Wood", NbtUtils.writeBlockState(wood));
    List<String> lines = getLinesSafe();
    for (int i = 0; i < 4; i++)
      tag.putString("Line" + i, lines.get(i));
  }
}
