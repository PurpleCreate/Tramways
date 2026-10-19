package purplecreate.tramways.mixins;

import de.mrjulsen.crn.api.core.snapshot.BoardEntry;
import de.mrjulsen.crn.api.core.snapshot.StopSnapshot;
import de.mrjulsen.crn.util.VariableManager;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(value = VariableManager.class, remap = false)
public interface VariableManagerAccessor {
  @Invoker("handleStopover")
  static String tramways$handleStopover(List<StopSnapshot> list, int i, String variable) {
    throw new AssertionError();
  }

  @Invoker("handleTrainEntry")
  static String tramways$handleTrainEntry(@Nullable BoardEntry data, String variable) {
    throw new AssertionError();
  }
}
