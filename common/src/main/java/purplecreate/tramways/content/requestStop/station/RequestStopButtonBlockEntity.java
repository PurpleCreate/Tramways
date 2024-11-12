package purplecreate.tramways.content.requestStop.station;

import com.simibubi.create.content.equipment.goggles.IHaveHoveringInformation;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.station.GlobalStation;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.LangBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import purplecreate.tramways.Tramways;
import purplecreate.tramways.content.announcements.info.TrainInfo;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RequestStopButtonBlockEntity extends SmartBlockEntity implements IHaveHoveringInformation {
  private BlockPos linkedStation;
  private boolean nearestTrain;
  private Component nearestTrainName;
  private String nearestTrainTerminus;

  public RequestStopButtonBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
  }

  public GlobalStation getLinkedStation() {
    if (
      level != null
        && linkedStation != null
        && level.getBlockEntity(linkedStation) instanceof StationBlockEntity sbe
    )
      return sbe.getStation();
    return null;
  }

  @Override
  public void addBehaviours(List<BlockEntityBehaviour> list) {}

  @Override
  public void tick() {
    super.tick();

    if (level.isClientSide)
      return;

    if (getLinkedStation() == null || getLinkedStation().getNearestTrain() == null) {
      if (nearestTrain) notifyUpdate();
      nearestTrain = false;

      if (getBlockState().getValue(RequestStopButtonBlock.POWERED))
        level.setBlock(
          worldPosition,
          getBlockState().setValue(RequestStopButtonBlock.POWERED, false),
          3
        );

      return;
    }

    Train train = getLinkedStation().getNearestTrain();
    TrainInfo trainInfo = TrainInfo.fromTrain(train);
    Map<String, String> props = trainInfo.getProperties();

    boolean changed =
      !nearestTrain
        || nearestTrainName != train.name
        || !Objects.equals(nearestTrainTerminus, props.get("end"));

    nearestTrain = true;
    nearestTrainName = train.name;
    nearestTrainTerminus = props.get("end");
    if (changed) notifyUpdate();
  }

  @Override
  public boolean addToTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
    if (!nearestTrain) {
      new LangBuilder(Tramways.ID)
        .add(
          Tramways.translatable("request_stop.next_train.no_trains")
            .withStyle(
              ChatFormatting.ITALIC,
              ChatFormatting.GRAY
            )
        )
        .forGoggles(tooltip);
      return true;
    }

    new LangBuilder(Tramways.ID)
      .add(
        Tramways.translatable("request_stop.next_train.title")
          .withStyle(ChatFormatting.AQUA)
      )
      .forGoggles(tooltip);

    new LangBuilder(Tramways.ID)
      .add(
        Tramways.translatable(
          "request_stop.next_train.info",
          nearestTrainName,
          nearestTrainTerminus
        )
          .withStyle(ChatFormatting.RESET)
      )
      .forGoggles(tooltip);

    return true;
  }

  @Override
  protected void read(CompoundTag tag, boolean clientPacket) {
    super.read(tag, clientPacket);
    linkedStation = NbtUtils.readBlockPos(tag.getCompound("LinkedStation"));
    nearestTrain = tag.getBoolean("NearestTrain");
    nearestTrainName = Component.Serializer.fromJson(tag.getString("NearestTrainName"));
    nearestTrainTerminus = tag.getString("NearestTrainTerminus");
  }

  @Override
  protected void write(CompoundTag tag, boolean clientPacket) {
    super.write(tag, clientPacket);

    if (linkedStation != null)
      tag.put("LinkedStation", NbtUtils.writeBlockPos(linkedStation));

    tag.putBoolean("NearestTrain", nearestTrain);
    tag.putString(
      "NearestTrainName",
      Component.Serializer.toJson(
        nearestTrain && nearestTrainName != null
          ? nearestTrainName
          : Components.empty()
      )
    );
    tag.putString(
      "NearestTrainTerminus",
      nearestTrain && nearestTrainTerminus != null
        ? nearestTrainTerminus
        : ""
    );
  }
}
