package purplecreate.tramways.content.announcements;

import com.simibubi.create.Create;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.schedule.ScheduleRuntime;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import de.mrjulsen.crn.api.core.RailwayBackendApi;
import de.mrjulsen.crn.api.core.query.BoardQuery;
import de.mrjulsen.crn.api.core.snapshot.BoardEntry;
import de.mrjulsen.crn.util.ModUtils;
import net.createmod.catnip.data.Pair;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import purplecreate.tramways.content.announcements.config.AnnouncementConfig;
import purplecreate.tramways.content.announcements.config.AnnouncementEvent;
import purplecreate.tramways.content.announcements.config.AnnouncementVariant;
import purplecreate.tramways.content.announcements.network.StationAnnouncementEventPacket;

import java.util.*;

public class SpeakerBlockEntity extends SmartBlockEntity {
  private String filter = null;
  private AnnouncementConfig config = null;

  private final Set<Pair<UUID, AnnouncementEvent>> announced = new HashSet<>();
  private final Map<UUID, String> announcedPlatform = new HashMap<>();

  public SpeakerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
  }

  @Override
  public void addBehaviours(List<BlockEntityBehaviour> list) {
  }

  public void configure(String filter, AnnouncementConfig config) {
    this.filter = filter;
    this.config = config;
  }

  @Override
  public void lazyTick() {
    if (level == null || level.isClientSide || filter == null || config == null) return;

    BoardQuery query = BoardQuery
      .defaults()
      .withLimit(16)
      .withCancelled(true);

    for (BoardEntry entry : RailwayBackendApi.getBoard(filter, query)) {
      Train train = Create.RAILWAYS.trains.get(entry.trainId());
      UUID tripId = ExtendedTrainData.getTripId(train);

      AnnouncementEvent event = null;

      String platform = entry.platform();
      String lastPlatform = announcedPlatform.get(tripId);

      if (entry.isCancelled()) {
        event = AnnouncementEvent.TRAIN_CANCELLED;
      } else if (entry.isDelayed()) {
        event = AnnouncementEvent.TRAIN_DELAYED;
      } else if (
        entry.terminus()
          && train.runtime.state == ScheduleRuntime.State.POST_TRANSIT
          && train.runtime.currentEntry == entry.entryIndex()
      ) {
        event = AnnouncementEvent.TERMINATING_TRAIN_STANDING;
      } else if (entry.realtime().arrivalIn(ModUtils.getTransformedWorldTime()) <= 60 * 20) {
        event = entry.terminus()
          ? AnnouncementEvent.TERMINATING_TRAIN_APPROACH
          : AnnouncementEvent.THROUGH_TRAIN_APPROACH;
      } else if (!Objects.equals(platform, lastPlatform)) {
        event = AnnouncementEvent.PLATFORM_CHANGED;
      }

      if (event == null || announced.contains(Pair.of(tripId, event))) continue;

      announced.add(Pair.of(tripId, event));
      announcedPlatform.put(tripId, platform);

      AnnouncementVariant variant = config.getVariantFor(event);
      if (variant == null) {
        continue;
      }

      CatnipServices.NETWORK.sendToClientsAround(
        (ServerLevel)level, worldPosition.getCenter(), 50,
        new StationAnnouncementEventPacket(worldPosition, variant, entry)
      );
    }

    Set<UUID> validTripIds = ExtendedTrainData.getTripIds();
    for (Iterator<Pair<UUID, AnnouncementEvent>> it = announced.iterator(); it.hasNext(); ) {
      UUID tripId = it.next().getFirst();
      if (validTripIds.contains(tripId)) continue;
      announcedPlatform.remove(tripId);
      it.remove();
    }
  }
}
