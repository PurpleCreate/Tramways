package purplecreate.tramways;

import com.simibubi.create.content.trains.graph.EdgePointType;

import purplecreate.tramways.content.signs.TramSignPoint;
import purplecreate.tramways.content.signs.demands.*;

public class TExtras {
  public static class EdgePointTypes {
    public static final EdgePointType<TramSignPoint> TRAM_SIGN =
      EdgePointType.register(Tramways.rl("tram_sign"), TramSignPoint::new);

    public static void register() {}
  }

  public static class SignDemands {
    public static void register() {
      SignDemand.register(Tramways.rl("speed"), new SpeedSignDemand());
      SignDemand.register(Tramways.rl("whistle"), new WhistleSignDemand());
      SignDemand.register(Tramways.rl("temporary_speed"), new TemporarySpeedSignDemand());
      SignDemand.register(Tramways.rl("temporary_end"), new TemporaryEndSignDemand());
    }
  }

  // runs in Tramways#commonSetup
  public static void registerCommon() {
    EdgePointTypes.register();
    SignDemands.register();
  }
}
