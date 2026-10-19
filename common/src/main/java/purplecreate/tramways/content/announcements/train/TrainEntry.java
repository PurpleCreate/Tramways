package purplecreate.tramways.content.announcements.train;

import com.simibubi.create.Create;
import com.simibubi.create.content.trains.entity.Train;
import de.mrjulsen.crn.api.core.snapshot.JourneySnapshot;
import de.mrjulsen.crn.api.core.snapshot.SectionSnapshot;
import de.mrjulsen.crn.api.core.snapshot.StopSnapshot;
import de.mrjulsen.crn.api.core.snapshot.TrainSnapshot;
import de.mrjulsen.crn.core.train.LiveTrainState;
import de.mrjulsen.crn.core.train.TrackedTrain;
import net.minecraft.nbt.CompoundTag;

import java.util.List;
import java.util.Optional;

public record TrainEntry(
  TrainSnapshot trainSnapshot,
  JourneySnapshot journeySnapshot
) {
  public static TrainEntry of(TrackedTrain train) {
    return new TrainEntry(
      TrainSnapshot.of(train),
      JourneySnapshot.of(train)
    );
  }

  public static TrainEntry fromNbt(CompoundTag tag) {
    return new TrainEntry(
      TrainSnapshot.fromNbt(tag.getCompound("Train")),
      JourneySnapshot.fromNbt(tag.getCompound("Journey"))
    );
  }

  public CompoundTag toNbt() {
    CompoundTag tag = new CompoundTag();
    tag.put("Train", trainSnapshot.toNbt());
    tag.put("Journey", journeySnapshot.toNbt());
    return tag;
  }

  public Train getTrain() {
    return Create.RAILWAYS.trains.get(trainSnapshot.trainId());
  }

  public boolean isWaitingAtStation() {
    return trainSnapshot != null && trainSnapshot.liveState() == LiveTrainState.AT_STATION;
  }

  public Optional<SectionSnapshot> getOperatingSection() {
    return journeySnapshot.operatingSection(isWaitingAtStation());
  }

  public String getTrainDisplayName() {
    return getOperatingSection()
      .filter(x -> x.line().hasName())
      .map(x -> x.line().name())
      .orElseGet(trainSnapshot::trainName);
  }

  public List<StopSnapshot> getRemainingStops() {
    List<StopSnapshot> service = journeySnapshot.stops();
    int current = journeySnapshot.currentStopIndex();
    return current < 0 ? service : service.subList(current, service.size());
  }

  public List<StopSnapshot> getStopovers() {
    List<StopSnapshot> remaining = getRemainingStops();
    int from = isWaitingAtStation() ? 1 : 0;
    return remaining.size() > from + 1 ? remaining.subList(from, remaining.size() - 1) : List.of();
  }
}
