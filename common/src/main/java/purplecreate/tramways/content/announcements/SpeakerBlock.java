package purplecreate.tramways.content.announcements;

import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import purplecreate.tramways.TNetworking;
import purplecreate.tramways.config.MessageConfig;
import purplecreate.tramways.content.announcements.network.PlayVoiceS2CPacket;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.utility.VoxelShaper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class SpeakerBlock extends DirectionalBlock implements IWrenchable {
  private static final VoxelShaper SHAPE =
    new AllShapes.Builder(box(2, 0, 2, 14, 1, 14)).forDirectional();

  public SpeakerBlock(Properties properties) {
    super(properties);
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(FACING);
  }

  @Nullable
  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    BlockState state = super.getStateForPlacement(context);
    if (state == null) return null;
    return state
      .setValue(FACING, context.getClickedFace());
  }

  @Override
  public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
    return SHAPE.get(state.getValue(FACING));
  }

  @Override
  public BlockState rotate(BlockState state, Rotation rotation) {
    return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
  }

  @Override
  public BlockState mirror(BlockState state, Mirror mirror) {
    return state.rotate(mirror.getRotation(state.getValue(FACING)));
  }

  @Override
  public InteractionResult onWrenched(BlockState state, UseOnContext context) {
    if (!context.getLevel().isClientSide) {
      TNetworking.sendToAll(
        new PlayVoiceS2CPacket(
          "en-GB-SoniaNeural",
          MessageConfig.simple("This is a test!"),
          context.getClickedPos()
        )
      );
    }

    return IWrenchable.super.onWrenched(state, context);
  }
}
