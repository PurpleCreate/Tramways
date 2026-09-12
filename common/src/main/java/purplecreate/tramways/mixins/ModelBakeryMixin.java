package purplecreate.tramways.mixins;

import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import purplecreate.tramways.Tramways;
import purplecreate.tramways.content.signals.render.SignalTypes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

@Mixin(ModelBakery.class)
public abstract class ModelBakeryMixin {
  @Shadow @Final private Map<ResourceLocation, BakedModel> bakedTopLevelModels;
  @Shadow @Final private Map<ResourceLocation, UnbakedModel> topLevelModels;
  @Shadow @Final private Map<ResourceLocation, UnbakedModel> unbakedCache;
  @Shadow public abstract UnbakedModel getModel(ResourceLocation modelLocation);
  @Shadow protected abstract BlockModel loadBlockModel(ResourceLocation location) throws IOException;

  @Inject(method = "<init>", at = @At("RETURN"))
  private void tramways$loadSignalTypes(BlockColors blockColors, ProfilerFiller profilerFiller, Map<ResourceLocation, BlockModel> map, Map<ResourceLocation, List<ModelBakery.LoadedJson>> map2, CallbackInfo ci) {
    profilerFiller.push("tramways_signal_types");
    List<UnbakedModel> unresolved = new ArrayList<>();

    SignalTypes.init(location -> {
      UnbakedModel unbakedModel;
      try {
        unbakedModel = loadBlockModel(location);
      } catch (Exception e) {
        Tramways.LOGGER.warn("Could not load model {}", location, e);
        unbakedModel = unbakedCache.get(ModelBakery.MISSING_MODEL_LOCATION);
      }

      this.unbakedCache.put(location, unbakedModel);
      this.topLevelModels.put(location, unbakedModel);
      unresolved.add(unbakedModel);
    });

    unresolved.forEach(unbakedModel ->
      unbakedModel.resolveParents(this::getModel)
    );

    profilerFiller.pop();
  }

  @Inject(method = "bakeModels", at = @At("TAIL"))
  private void tramways$notifyBaked(BiFunction<ResourceLocation, Material, TextureAtlasSprite> biFunction, CallbackInfo ci) {
    SignalTypes.onBaked(bakedTopLevelModels);
  }
}
