package purplecreate.tramways.mixins;

import com.simibubi.create.content.trains.bogey.AbstractBogeyBlock;
import com.simibubi.create.content.trains.entity.CarriageBogey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CarriageBogey.class)
public interface CarriageBogeyAccessor {
  @Accessor("type")
  AbstractBogeyBlock<?> tramways$getType();
}
