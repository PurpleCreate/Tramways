package purplecreate.tramways.mixinInterfaces;

import com.simibubi.create.content.trains.graph.DiscoveredPath;

public interface ITramNavigation {
  void tramways$resetRouteCancelled();

  void tramways$setRouteThroughJunction(DiscoveredPath path, Runnable onComplete);
  void tramways$cancelRouteThroughJunction();
}
