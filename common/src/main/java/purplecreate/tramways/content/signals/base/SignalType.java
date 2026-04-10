package purplecreate.tramways.content.signals.base;

import com.simibubi.create.AllShapes;
import net.createmod.catnip.math.VoxelShaper;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;
import purplecreate.tramways.content.signals.item.ItemAnimator;

import static net.minecraft.world.level.block.Block.box;

public abstract class SignalType {
  public static class Shapes {
    public static final VoxelShaper TRAM = new AllShapes.Builder(box(2, 2, 14, 14, 14, 16)).forHorizontal(Direction.NORTH);
    public static final VoxelShaper FOUR_ASPECT = new AllShapes.Builder(box(2, -1, 12, 14, 30, 16)).forHorizontal(Direction.NORTH);
  }

  public abstract void animateItem(ItemAnimator animator);
  public abstract void addAspects(Aspect.Builder aspects);
  public abstract float getSignalFaceZ();
  public abstract VoxelShape getFaceShape(Direction direction);
}
