package purplecreate.tramways.content.signs;

import com.simibubi.create.content.trains.signal.SignalBlockEntity;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import purplecreate.tramways.TExtras;
import purplecreate.tramways.Tramways;
import purplecreate.tramways.content.signs.demands.SignDemand;
import com.simibubi.create.api.contraption.transformable.TransformableBlockEntity;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.trains.track.TrackTargetingBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class TramSignBlockEntity extends SmartBlockEntity implements TransformableBlockEntity {
  TrackTargetingBehaviour<TramSignPoint> edgePoint;

  private static final ResourceLocation DEFAULT_DEMAND_ID = Tramways.rl("speed");
  private static final ResourceLocation DEFAULT_AUX_DEMAND_ID = Tramways.rl("advance_warning_aux");
  private SignDemand demand;
  private CompoundTag demandExtra;
  private SignalBlockEntity.OverlayState overlay;

  public TramSignBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);

    demand = SignDemand.demands.get(
      getSignType() == TramSignBlock.SignType.AUXILIARY
        ? DEFAULT_AUX_DEMAND_ID
        : DEFAULT_DEMAND_ID
    );
    demandExtra = new CompoundTag();
    demand.setDefaultSettings(demandExtra);
  }

  @Override
  public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    behaviours.add(edgePoint = new TrackTargetingBehaviour<>(this, TExtras.EdgePointTypes.TRAM_SIGN));
  }

  @Override
  public void transform(BlockEntity be, StructureTransform transform) {
    edgePoint.transform(be, transform);
  }

  public SignDemand getDemand() {
    return demand;
  }

  public CompoundTag getDemandExtra() {
    return demandExtra;
  }

  public TramSignBlock.SignType getSignType() {
    Block block = getBlockState().getBlock();

    if (block instanceof TramSignBlock)
      return ((TramSignBlock) block).signType;

    return TramSignBlock.SignType.TRAM;
  }

  public SignalBlockEntity.OverlayState getOverlay() {
    return overlay;
  }

  // warning: this function receives unvalidated data from the client
  public void setDemand(ResourceLocation id) {
    ResourceLocation lastId = demand != null ? demand.id : null;

    demand = SignDemand.demands.get(
      SignDemand.demands.containsKey(id)
        ? id
        : DEFAULT_DEMAND_ID
    );

    if (!demand.id.equals(lastId)) {
      demandExtra = new CompoundTag();
      demand.setDefaultSettings(demandExtra);
    }

    notifyUpdate();
    updatePoint();
  }

  // warning: this function also receives unvalidated data from the client
  public void setDemandExtra(CompoundTag received) {
    if (demand == null) return;

    CompoundTag disk = new CompoundTag();
    demand.validateSettings(received, disk);
    demandExtra = disk;

    notifyUpdate();
    updatePoint();
  }

  private void updatePoint() {
    TramSignPoint point = edgePoint.getEdgePoint();
    if (point != null)
      point.updateSignData(worldPosition, demand, demandExtra);
  }

  public void setOverlay(SignalBlockEntity.OverlayState overlay) {
    if (this.overlay == overlay)
      return;
    this.overlay = overlay;
    notifyUpdate();
  }

  @Override
  public void tick() {
    super.tick();

    if (level.isClientSide) return;

    TramSignPoint point = edgePoint.getEdgePoint();

    if (point == null) {
      setOverlay(SignalBlockEntity.OverlayState.RENDER);
      return;
    } else {
      setOverlay(point.getOverlayFor(worldPosition));
    }

    TramSignPoint.SignData data = point.getSignData(worldPosition);

    if (data.demand != demand || data.demandExtra != demandExtra) {
      demand = data.demand;
      demandExtra = data.demandExtra;
      notifyUpdate();
    }
  }

  @Override
  protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
    super.read(tag, registries, clientPacket);

    overlay = NBTHelper.readEnum(tag, "Overlay", SignalBlockEntity.OverlayState.class);

    if (tag.contains("Demand"))
      demand = SignDemand.demands.get(NBTHelper.readResourceLocation(tag, "Demand"));

    if (tag.contains("DemandExtra")) {
      demandExtra = tag.getCompound("DemandExtra");
    } else {
      demandExtra = new CompoundTag();
      demand.setDefaultSettings(demandExtra);
    }
  }

  @Override
  protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
    super.write(tag, registries, clientPacket);

    NBTHelper.writeEnum(
      tag,
      "Overlay",
      overlay == null
        ? SignalBlockEntity.OverlayState.SKIP
        : overlay
    );

    if (demand != null)
      NBTHelper.writeResourceLocation(tag, "Demand", demand.id);
    if (demandExtra != null)
      tag.put("DemandExtra", demandExtra);
  }

  @Override
  protected AABB createRenderBoundingBox() {
    return new AABB(
      worldPosition.getCenter(),
      edgePoint.getGlobalPosition().getCenter()
    ).inflate(2);
  }
}
