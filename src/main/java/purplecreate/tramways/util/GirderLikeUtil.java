package purplecreate.tramways.util;

import com.simibubi.create.AllBlocks;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.world.level.block.state.BlockState;
import purplecreate.tramways.TBlocks;

import java.util.List;

public class GirderLikeUtil {
  private static List<BlockEntry> allowed = List.of(
    TBlocks.TRAM_SIGN,
    TBlocks.RAILWAY_SIGN,
    TBlocks.AUXILIARY_SIGN,
    TBlocks.TRAM_SIGNAL,
    AllBlocks.METAL_GIRDER
  );

  public static boolean allowed(BlockState state, boolean includeGirder) {
    if (!includeGirder && AllBlocks.METAL_GIRDER.has(state))
      return false;

    return allowed.stream().anyMatch(entry -> entry.has(state));
  }
}
