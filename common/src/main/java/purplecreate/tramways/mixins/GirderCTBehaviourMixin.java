package purplecreate.tramways.mixins;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.content.decoration.girder.GirderBlock;
import com.simibubi.create.content.decoration.girder.GirderCTBehaviour;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import purplecreate.tramways.TBlocks;

import java.util.List;

@Mixin(value = GirderCTBehaviour.class, remap = false)
public class GirderCTBehaviourMixin {
  @Unique private static List<BlockEntry> tramways$allowed = List.of(
    TBlocks.TRAM_SIGN,
    TBlocks.RAILWAY_SIGN,
    TBlocks.TRAM_SIGNAL,
    AllBlocks.METAL_GIRDER
  );

  @Unique
  private static boolean tramways$allowed(BlockState state) {
    return tramways$allowed.stream().anyMatch(entry -> entry.has(state));
  }

  @Inject(method = "getShift", at = @At("HEAD"), cancellable = true)
  private void tramways$girderShift(
    BlockState state,
    Direction direction,
    TextureAtlasSprite sprite,
    CallbackInfoReturnable<CTSpriteShiftEntry> cir
  ) {
    if (tramways$allowed(state) && !AllBlocks.METAL_GIRDER.has(state))
      cir.setReturnValue(AllSpriteShifts.GIRDER_POLE);
  }

  @Inject(method = "connectsTo", at = @At("HEAD"), cancellable = true)
  private void tramways$girderConnections(
    BlockState state,
    BlockState other,
    BlockAndTintGetter reader,
    BlockPos pos,
    BlockPos otherPos,
    Direction face,
    CallbackInfoReturnable<Boolean> cir
  ) {
    if (AllBlocks.METAL_GIRDER.has(state) && state.getBlock() == other.getBlock())
      return; // don't handle this

    if (tramways$allowed(state) && tramways$allowed(other))
      cir.setReturnValue(
        AllBlocks.METAL_GIRDER.has(other)
          ? !other.getValue(GirderBlock.X) && !other.getValue(GirderBlock.Z)
          : true
      );
  }
}
