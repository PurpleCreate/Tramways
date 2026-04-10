package purplecreate.tramways.content.signals.types;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.phys.shapes.VoxelShape;
import purplecreate.tramways.content.signals.base.Aspect;
import purplecreate.tramways.content.signals.base.ExtendedSignalState;
import purplecreate.tramways.content.signals.base.SignalType;
import purplecreate.tramways.content.signals.item.ItemAnimator;

public class FourAspectSignalType extends SignalType {
  @Override
  public void animateItem(ItemAnimator animator) {
    animator.setSignal(ExtendedSignalState.DANGER);
    animator.sleep(1);
    animator.setSignal(ExtendedSignalState.CAUTION);
    animator.sleep(1);
    animator.setSignal(ExtendedSignalState.PRE_CAUTION);
    animator.sleep(1);
    animator.setSignal(ExtendedSignalState.CLEAR);
    animator.sleep(1);
  }

  @Override
  @Environment(EnvType.CLIENT)
  public void addAspects(Aspect.Builder aspects) {
    aspects.addSimple(8, -9, 3, DyeColor.YELLOW, holder ->
      holder.getSignalState() == ExtendedSignalState.PRE_CAUTION
    );
    aspects.addSimple(8, -2, 3, DyeColor.GREEN, holder ->
      holder.getSignalState() == ExtendedSignalState.CLEAR
    );
    aspects.addSimple(8, 5, 3, DyeColor.YELLOW, holder ->
      holder.getSignalState() == ExtendedSignalState.CAUTION
        || holder.getSignalState() == ExtendedSignalState.PRE_CAUTION
    );
    aspects.addSimple(8, 12, 3, DyeColor.RED, holder ->
      holder.getSignalState() == ExtendedSignalState.DANGER
        || holder.getSignalState() == ExtendedSignalState.NO_ROUTE_SET
    );
  }

  @Override
  public float getSignalFaceZ() {
    return 1;
  }

  @Override
  public VoxelShape getFaceShape(Direction direction) {
    return Shapes.FOUR_ASPECT.get(direction);
  }
}
