package purplecreate.tramways.mixins;

import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = CarriageContraptionEntity.class, remap = false)
public interface CarriageContraptionEntityAccessor {
  @Invoker("sendPrompt")
  void tramways$sendPrompt(Player player, MutableComponent component, boolean shadow);
}
