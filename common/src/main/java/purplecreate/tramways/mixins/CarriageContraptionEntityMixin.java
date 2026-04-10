package purplecreate.tramways.mixins;

import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;

@Mixin(value = CarriageContraptionEntity.class, remap = false)
public class CarriageContraptionEntityMixin {
  @Inject(method = "control", at = @At(value = "FIELD", target = "Lcom/simibubi/create/content/trains/entity/Train;targetSpeed:D"))
  private void tramways$promptForRoute(BlockPos controlsLocalPos, Collection<Integer> heldControls, Player player, CallbackInfoReturnable<Boolean> cir) {
    
  }
}
