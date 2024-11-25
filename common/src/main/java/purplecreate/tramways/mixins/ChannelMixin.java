package purplecreate.tramways.mixins;

import com.mojang.blaze3d.audio.Channel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Channel.class)
public interface ChannelMixin {
  @Invoker("create")
  static Channel createChannel() {
    throw new AssertionError();
  }
}
