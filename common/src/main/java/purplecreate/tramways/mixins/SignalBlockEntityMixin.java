package purplecreate.tramways.mixins;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.trains.signal.SignalBlockEntity;
import net.createmod.catnip.lang.LangBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import purplecreate.tramways.Tramways;
import purplecreate.tramways.content.signals.base.JunctionState;
import purplecreate.tramways.mixinInterfaces.IRoutedSignal;
import purplecreate.tramways.mixinInterfaces.IRoutedSignalBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Mixin(value = SignalBlockEntity.class, remap = false)
public abstract class SignalBlockEntityMixin implements IHaveGoggleInformation, IRoutedSignalBlock {
  @Unique private JunctionState tramways$route = null;

  @Override
  public JunctionState tramways$getRoute() {
    return tramways$route;
  }

  @Override
  public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
    if (tramways$route == null || tramways$route.getName().getString().isBlank()) {
      return false;
    } else {
      new LangBuilder(Tramways.ID).add(tramways$route.getName()).forGoggles(tooltip);
      return true;
    }
  }

  @Inject(method = "tick", at = @At("HEAD"))
  private void tramways$cacheRoute(CallbackInfo ci) {
    if (!((Object)this instanceof SignalBlockEntity be)) return;
    if (be.getLevel() == null || be.getLevel().isClientSide) return;
    if (!(be.getSignal() instanceof IRoutedSignal signal)) return;

    JunctionState next = signal.tramways$getRoute(be.getBlockPos());

    if (!Objects.equals(next, tramways$route)) {
      tramways$route = next;
      be.notifyUpdate();
    }
  }

  @Inject(method = "read", at = @At("HEAD"))
  private void tramways$read(CompoundTag tag, boolean clientPacket, CallbackInfo ci) {
    tramways$route = JunctionState.fromNbt(tag.getCompound("Tramways$Route"));
  }

  @Inject(method = "write", at = @At("HEAD"))
  private void tramways$write(CompoundTag tag, boolean clientPacket, CallbackInfo ci) {
    if (tramways$route != null) tag.put("Tramways$Route", tramways$route.toNbt());
  }
}
