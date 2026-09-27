package purplecreate.tramways.content.signals;

import net.minecraft.util.StringRepresentable;

public enum TramSignalState implements StringRepresentable {
  INVALID("invalid"),
  FORWARD("forward"),
  LEFT("left"),
  RIGHT("right"),
  STOP("stop");

  final String state;

  TramSignalState(String state) {
    this.state = state;
  }

  @Override
  public String getSerializedName() {
    return state;
  }
}
