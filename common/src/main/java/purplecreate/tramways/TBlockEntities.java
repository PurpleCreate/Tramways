package purplecreate.tramways;

import com.tterrag.registrate.util.entry.BlockEntityEntry;

import purplecreate.tramways.content.requestStop.station.RequestStopButtonBlockEntity;
import purplecreate.tramways.content.signals.TramSignalBlockEntity;
import purplecreate.tramways.content.signs.TramSignBlockEntity;
import purplecreate.tramways.content.signs.TramSignRenderer;
import purplecreate.tramways.content.stationDeco.nameSign.NameSignBlockEntity;
import purplecreate.tramways.content.stationDeco.nameSign.NameSignRenderer;

public class TBlockEntities {
  public static final BlockEntityEntry<TramSignalBlockEntity> TRAM_SIGNAL =
    Tramways.REGISTRATE.blockEntity("tram_signal", TramSignalBlockEntity::new)
      .validBlocks(TBlocks.TRAM_SIGNAL)
      .register();

  public static final BlockEntityEntry<TramSignBlockEntity> TRAM_SIGN =
    Tramways.REGISTRATE.blockEntity("tram_sign", TramSignBlockEntity::new)
      .validBlocks(TBlocks.TRAM_SIGN, TBlocks.RAILWAY_SIGN, TBlocks.AUXILIARY_SIGN)
      .renderer(() -> TramSignRenderer::new)
      .register();

  public static final BlockEntityEntry<RequestStopButtonBlockEntity> REQUEST_STOP_BUTTON =
    Tramways.REGISTRATE.blockEntity("request_stop_button", RequestStopButtonBlockEntity::new)
      .validBlocks(TBlocks.REQUEST_STOP_BUTTON)
      .register();

  public static final BlockEntityEntry<NameSignBlockEntity> STATION_NAME_SIGN =
    Tramways.REGISTRATE.blockEntity("station_name_sign", NameSignBlockEntity::new)
      .validBlocks(TBlocks.STATION_NAME_SIGNS.toArray())
      .renderer(() -> NameSignRenderer::new)
      .register();

  public static void register() {}
}
