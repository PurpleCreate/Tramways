package purplecreate.tramways.content.signals.types;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.phys.shapes.VoxelShape;
import purplecreate.tramways.content.signals.base.Aspect;
import purplecreate.tramways.content.signals.base.JunctionState;
import purplecreate.tramways.content.signals.base.SignalType;
import purplecreate.tramways.content.signals.item.ItemAnimator;

public class TheatreSignalType extends SignalType {
  @Override
  public void animateItem(ItemAnimator animator) {
    for (int i = 0; i < 26; i++) {
      animator.setRoute(new JunctionState((char)(i + 65), Component.empty(), JunctionState.DirectionalJunctionState.NONE));
      animator.sleep(1);
    }
  }

  @Override
  @Environment(EnvType.CLIENT)
  public void addAspects(Aspect.Builder aspects) {
    aspects.addString(8, 8, .8f, DyeColor.WHITE, holder -> {
      JunctionState state = holder.getJunctionState();
      if (state == null) return null;
      return String.valueOf(state.getLetter());
    });
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
