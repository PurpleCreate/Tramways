package purplecreate.tramways.util;

import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;

import java.util.function.Consumer;

public class MovementBehaviourUtil {
  public static void withCarriage(MovementContext ctx, Consumer<Carriage> callback) {
    if (!ctx.world.isClientSide && ctx.contraption.entity instanceof CarriageContraptionEntity cce)
      callback.accept(cce.getCarriage());
  }
}
