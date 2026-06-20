package purplecreate.tramways.content.signals.base;

import com.simibubi.create.content.trains.signal.SignalBoundary;
import net.createmod.catnip.data.Pair;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import purplecreate.tramways.Tramways;

import java.util.Objects;

public class JunctionState {
  private final char letter;
  private final Component name;
  private final DirectionalJunctionState direction;

  public JunctionState(char letter, Component name, DirectionalJunctionState direction) {
    this.letter = letter;
    this.name = name;
    this.direction = direction;
  }

  public static JunctionState unknown() {
    return new JunctionState('?', Tramways.translatable("junction_state.unknown"), DirectionalJunctionState.NONE);
  }

  public char getLetter() {
    return letter;
  }

  public Component getName() {
    return name;
  }

  public DirectionalJunctionState getDirection() {
    return direction;
  }

  public static JunctionState fromNbt(CompoundTag tag) {
    if (tag.isEmpty()) return null;
    return new JunctionState(
      tag.getString("Letter").charAt(0),
      Component.Serializer.fromJson(tag.getString("Name")),
      NBTHelper.readEnum(tag, "Direction", DirectionalJunctionState.class)
    );
  }

  public CompoundTag toNbt() {
    CompoundTag tag = new CompoundTag();
    tag.putString("Letter", String.valueOf(letter));
    tag.putString("Name", Component.Serializer.toJson(name));
    NBTHelper.writeEnum(tag, "Direction", direction);
    return tag;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof JunctionState other)) return false;
    return this.letter == other.letter && Objects.equals(this.name, other.name) && this.direction == other.direction;
  }

  @Override
  public String toString() {
    return "JunctionState{letter=%s, name=%s, direction=%s}".formatted(letter, name, direction);
  }

  public enum DirectionalJunctionState {
    LEFT_135,
    LEFT_90,
    LEFT_45,
    NONE,
    RIGHT_45,
    RIGHT_90,
    RIGHT_135
  }

  public record SignalInfo(SignalBoundary signal, Boolean forward, JunctionState state) {
    public Pair<SignalBoundary, Boolean> asPair() {
      return Pair.of(signal, forward);
    }
  }
}
