package purplecreate.tramways;

import com.tterrag.registrate.util.entry.BlockEntityEntry;

import purplecreate.tramways.content.signals.TramSignalBlockEntity;
import purplecreate.tramways.content.signs.TramSignBlockEntity;
import purplecreate.tramways.content.signs.TramSignRenderer;

public class TBlockEntities {
  public static final BlockEntityEntry<TramSignalBlockEntity> TRAM_SIGNAL =
    Tramways.REGISTRATE.blockEntity("tram_signal", TramSignalBlockEntity::new)
      .validBlocks(TBlocks.TRAM_SIGNAL)
      .register();

  public static final BlockEntityEntry<TramSignBlockEntity> TRAM_SIGN =
    Tramways.REGISTRATE.blockEntity("tram_sign", TramSignBlockEntity::new)
      .validBlocks(TBlocks.TRAM_SIGN, TBlocks.RAILWAY_SIGN)
      .renderer(() -> TramSignRenderer::new)
      .register();

  public static void register() {}
}
