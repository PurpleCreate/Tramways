package purplecreate.tramways.mixinInterfaces;

import purplecreate.tramways.content.signals.base.JunctionState;

public interface IRoutedSignalBlock {
  JunctionState tramways$getRoute();
}
