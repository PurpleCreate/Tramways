package purplecreate.tramways.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.Navigation;
import com.simibubi.create.content.trains.station.GlobalStation;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import purplecreate.tramways.content.signals.manual.RouteSelectionHUD;

import java.util.Collection;

@Mixin(value = CarriageContraptionEntity.class, remap = false)
public class CarriageContraptionEntityMixin {
  @Unique private final RouteSelectionHUD tramways$routeHUD = new RouteSelectionHUD((CarriageContraptionEntity)(Object)this);

  @WrapOperation(method = "control", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/entity/Navigation;findNearestApproachable(Z)Lcom/simibubi/create/content/trains/station/GlobalStation;"))
  private GlobalStation tramways$promptForRoute(Navigation instance, boolean forward, Operation<GlobalStation> original, @Local(argsOnly = true) Collection<Integer> heldControls, @Local(argsOnly = true) Player player) {
    if (tramways$routeHUD.control(forward, heldControls, player)) {
      return null;
    } else {
      return original.call(instance, forward);
    }
  }
}
