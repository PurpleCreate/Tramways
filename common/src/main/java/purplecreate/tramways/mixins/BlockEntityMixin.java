package purplecreate.tramways.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import purplecreate.tramways.TBlockEntities;
import purplecreate.tramways.Tramways;

import java.util.Set;

@Mixin(BlockEntity.class)
public class BlockEntityMixin {
  @Inject(method = "loadStatic", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/resources/ResourceLocation;tryParse(Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;"), cancellable = true)
  private static void tramways$remap(BlockPos pos, BlockState state, CompoundTag tag, CallbackInfoReturnable<BlockEntity> cir, @Local LocalRef<ResourceLocation> idRef) {
    ResourceLocation id = idRef.get();
    if (id == null) return;

    if (id.getNamespace().equals(Tramways.ID)) {
      if (Set.of("four_aspect_signal", "theatre_signal", "tram_signal").contains(id.getPath())) {
        idRef.set(TBlockEntities.GENERIC_SIGNAL.getId());
        NBTHelper.writeResourceLocation(tag, "SignalType", id.getPath().equals("tram_signal") ? Tramways.rl("lrv_signal") : id);
      }
    }
  }
}
