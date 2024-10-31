package purplecreate.tramways.content.announcements.sound;

import com.simibubi.create.content.contraptions.OrientedContraptionEntity;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.io.InputStream;

public class MovingVoiceSoundInstance extends VoiceSoundInstance implements TickableSoundInstance {
  final Carriage carriage;
  final BlockPos localPos;

  boolean stopped = false;

  protected MovingVoiceSoundInstance(InputStream stream, Carriage carriage, BlockPos localPos) {
    super(stream, BlockPos.ZERO);

    this.carriage = carriage;
    this.localPos = localPos;
  }

  @ExpectPlatform
  public static MovingVoiceSoundInstance create(InputStream stream, Carriage carriage, BlockPos localPos) {
    throw new AssertionError();
  }

  private StructureTransform makeStructureTransform(OrientedContraptionEntity e) {
    Vec3 v = e.getAnchorVec().add(.5, .5, .5);
    BlockPos offset = new BlockPos(Mth.floor(v.x), Mth.floor(v.y), Mth.floor(v.z));
    return new StructureTransform(offset, 0f, -e.yaw + e.getInitialYaw(), 0f);
  }

  private void stop() {
    stopped = true;
    looping = false;
  }

  @Override
  public boolean isStopped() {
    return stopped;
  }

  @Override
  public void tick() {
    Level level = Minecraft.getInstance().level;
    if (level == null) {
      stop();
      return;
    }

    Carriage.DimensionalCarriageEntity dce = carriage.getDimensionalIfPresent(level.dimension());
    if (dce == null) {
      stop();
      return;
    }

    CarriageContraptionEntity cce = dce.entity.get();
    if (cce == null) {
      stop();
      return;
    }

    BlockPos realPos = makeStructureTransform(cce).apply(localPos);
    x = realPos.getX();
    y = realPos.getY();
    z = realPos.getZ();
  }
}
