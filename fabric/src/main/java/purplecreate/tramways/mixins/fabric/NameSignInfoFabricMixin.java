package purplecreate.tramways.mixins.fabric;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.spongepowered.asm.mixin.Mixin;
import purplecreate.tramways.Tramways;
import purplecreate.tramways.content.stationDeco.nameSign.info.NameSignInfo;

@Mixin(NameSignInfo.class)
public abstract class NameSignInfoFabricMixin implements ResourceManagerReloadListener, IdentifiableResourceReloadListener {
  @Override
  public ResourceLocation getFabricId() {
    return Tramways.rl("name_sign_info");
  }
}
