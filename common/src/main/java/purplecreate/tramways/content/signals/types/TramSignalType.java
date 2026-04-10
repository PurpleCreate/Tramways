package purplecreate.tramways.content.signals.types;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.phys.shapes.VoxelShape;
import purplecreate.tramways.content.signals.base.StateHolder;
import purplecreate.tramways.content.signals.base.Aspect;
import purplecreate.tramways.content.signals.base.ExtendedSignalState;
import purplecreate.tramways.content.signals.base.SignalType;
import purplecreate.tramways.content.signals.item.ItemAnimator;

import java.util.function.Function;

public class TramSignalType extends SignalType {
  @Override
  public void animateItem(ItemAnimator animator) {
    animator.setSignal(ExtendedSignalState.CLEAR);
  }

  @Override
  @Environment(EnvType.CLIENT)
  public void addAspects(Aspect.Builder aspects) {
    Function<StateHolder, Boolean> valid = holder ->
      holder.getSignalState() != ExtendedSignalState.INVALID && holder.getSignalState() != ExtendedSignalState.NO_ROUTE_SET;
    Function<StateHolder, Boolean> stop = holder ->
      holder.getSignalState() == ExtendedSignalState.DANGER || holder.getSignalState() == ExtendedSignalState.NO_ROUTE_SET;
    Function<StateHolder, Boolean> clear = holder ->
      valid.apply(holder) && holder.getSignalState() != ExtendedSignalState.DANGER;

    float c = 8;
    float r = 0.33f;
    float r4 = r * 4;
    float r8 = r * 8;

    aspects.addSimple(c, c, r, DyeColor.WHITE, valid);

    aspects.addSimple(c, c - r8, r, DyeColor.WHITE, clear);
    aspects.addSimple(c, c - r4, r, DyeColor.WHITE, clear);
    aspects.addSimple(c, c + r4, r, DyeColor.WHITE, clear);
    aspects.addSimple(c, c + r8, r, DyeColor.WHITE, clear);

    aspects.addSimple(c - r8, c, r, DyeColor.WHITE, stop);
    aspects.addSimple(c - r4, c, r, DyeColor.WHITE, stop);
    aspects.addSimple(c + r4, c, r, DyeColor.WHITE, stop);
    aspects.addSimple(c + r8, c, r, DyeColor.WHITE, stop);
  }

  @Override
  public float getSignalFaceZ() {
    return 1;
  }

  @Override
  public VoxelShape getFaceShape(Direction direction) {
    return Shapes.TRAM.get(direction);
  }
}
