package purplecreate.tramways.content.announcements;

import com.simibubi.create.Create;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.schedule.ScheduleRuntime;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import de.mrjulsen.crn.data.train.TrainStop;
import de.mrjulsen.crn.data.train.TrainUtils;
import de.mrjulsen.crn.data.train.portable.StationDisplayData;
import net.createmod.catnip.data.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import purplecreate.tramways.TNetworking;
import purplecreate.tramways.Tramways;
import purplecreate.tramways.content.announcements.config.AnnouncementConfig;
import purplecreate.tramways.content.announcements.config.AnnouncementEvent;
import purplecreate.tramways.content.announcements.config.AnnouncementVariant;
import purplecreate.tramways.content.announcements.network.StationAnnouncementEventS2CPacket;

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

    for (TrainStop prediction : TrainUtils.getDeparturesAtStationName(filter, null, false, false)) {
      Train train = Create.RAILWAYS.trains.get(prediction.getTrainId());
      UUID tripId = ExtendedTrainData.getTripId(train);
      StationDisplayData displayData = StationDisplayData.of(prediction);

      AnnouncementEvent event = null;

      String platform = prediction.getRealTimeStationTag().info().platform();
      String lastPlatform = announcedPlatform.get(tripId);

      if (displayData.getTrainData().isCancelled()) {
        event = AnnouncementEvent.TRAIN_CANCELLED;
      } else if (prediction.isDepartureDelayed()) {
        event = AnnouncementEvent.TRAIN_DELAYED;
      } else if (
        displayData.isLastStop()
          && train.runtime.state == ScheduleRuntime.State.POST_TRANSIT
          && train.runtime.currentEntry == prediction.getScheduleIndex()
      ) {
        event = AnnouncementEvent.TERMINATING_TRAIN_STANDING;
      } else if (prediction.getTicksUntilArrival() <= 60 * 20) {
        event = displayData.isLastStop()
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

      TNetworking.sendToNear(
        new StationAnnouncementEventS2CPacket(worldPosition, variant, displayData),
        worldPosition.getCenter(), 50, level.dimension()
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
