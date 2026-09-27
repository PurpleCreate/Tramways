package purplecreate.tramways.mixins;

import com.simibubi.create.content.redstone.displayLink.DisplayLinkBlockEntity;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkConfigurationPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import purplecreate.tramways.content.announcements.station.StationSpeakerDisplaySource;

@Mixin(value = DisplayLinkConfigurationPacket.class, remap = false)
public class DisplayLinkConfigurationPacketMixin {
  @Shadow private CompoundTag configData;

  @Inject(method = "applySettings(Lnet/minecraft/server/level/ServerPlayer;Lcom/simibubi/create/content/redstone/displayLink/DisplayLinkBlockEntity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompoundTag;merge(Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/nbt/CompoundTag;", remap = true), cancellable = true)
  private void tramways$saveConfig(ServerPlayer player, DisplayLinkBlockEntity be, CallbackInfo ci) {
    if (be.activeSource instanceof StationSpeakerDisplaySource) {
      ci.cancel();

      for (String key : configData.getAllKeys()) {
        be.getSourceConfig().put(key, configData.get(key).copy());
      }
      be.updateGatheredData();
      be.notifyUpdate();
    }
  }
}
