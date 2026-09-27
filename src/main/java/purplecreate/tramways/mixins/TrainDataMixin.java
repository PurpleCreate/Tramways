package purplecreate.tramways.mixins;

import com.simibubi.create.content.trains.entity.Train;
import de.mrjulsen.crn.data.train.TrainData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import purplecreate.tramways.content.announcements.ExtendedTrainData;

@Mixin(value = TrainData.class, remap = false)
public class TrainDataMixin {
  @Shadow @Final private transient Train train;

  @Inject(method = "changeCurrentSection", at = @At("HEAD"))
  public void tramways$beginTrip(int sectionEntryIndex, CallbackInfo ci) {
    ExtendedTrainData.beginTrip(train);
  }
}
