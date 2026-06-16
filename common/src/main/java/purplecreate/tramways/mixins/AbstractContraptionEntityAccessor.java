package purplecreate.tramways.mixins;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.StructureTransform;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractContraptionEntity.class)
public interface AbstractContraptionEntityAccessor {
  @Invoker("makeStructureTransform")
  StructureTransform tramways$makeStructureTransform();
}
