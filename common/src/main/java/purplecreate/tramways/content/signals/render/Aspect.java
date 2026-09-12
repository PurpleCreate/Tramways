package purplecreate.tramways.content.signals.render;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.jexl3.*;
import org.apache.commons.jexl3.introspection.JexlPermissions;
import purplecreate.tramways.Tramways;
import purplecreate.tramways.content.signals.base.ExtendedSignalState;
import purplecreate.tramways.content.signals.base.JunctionState;
import purplecreate.tramways.util.CodecUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Environment(EnvType.CLIENT)
public abstract class Aspect {
  private static final JexlEngine JEXL = new JexlBuilder()
    .features(JexlFeatures.createAll())
    .permissions(JexlPermissions.UNRESTRICTED)
    .create();
  protected static Set<String> INVALID_FUNCTIONS = new HashSet<>();

  protected String type;
  protected Vec3 center;

  public static Aspect fromJson(JsonElement json) {
    JsonObject obj = json.getAsJsonObject();
    String type = obj.get("type").getAsString();
    Aspect result = Aspects.create(type);

    result.type = type;
    result.center = CodecUtil.fromJson(Vec3.CODEC, obj.get("center"));
    result.deserialize(obj);

    return result;
  }

  protected <T> ConfigFunction<T> parseFunction(String function, Class<T> cast) {
    JexlScript script = JEXL.createScript(function, "signal", "junction");

    return (signal, junction) -> {
      if (INVALID_FUNCTIONS.contains(function)) return null;

      // setup
      JexlContext context = new MapContext();

      for (Map.Entry<String, Color> color : Color.REGISTRY.entrySet()) {
        context.set(color.getKey(), color.getValue());
      }

      for (ExtendedSignalState state : ExtendedSignalState.values()) {
        context.set(state.name().toLowerCase(), state);
      }

      for (JunctionState.DirectionalJunctionState state : JunctionState.DirectionalJunctionState.values()) {
        context.set(state.name().toLowerCase(), state);
      }

      // execute
      try {
        Object value = script.execute(context, signal, junction);
        if (value == null || cast.isInstance(value)) {
          return (T)value;
        }

        throw new AssertionError("User defined function returned " + value.getClass().getName() + ", expected " + cast.getName());
      } catch (Throwable e) {
        Tramways.LOGGER.warn("Failed to execute user defined function", e);
        INVALID_FUNCTIONS.add(function);
        return null;
      }
    };
  }

  public abstract Aspect copy();
  public abstract void tick(ExtendedSignalState signal, JunctionState junction);
  public abstract void render(Renderer renderer, float partialTicks, int light, int overlay);
  protected abstract void deserialize(JsonObject obj);

  @FunctionalInterface
  public interface ConfigFunction<T> {
    T execute(ExtendedSignalState signal, JunctionState junction);
  }

  public record Color(float h, float s, float b) {
    static final Map<String, Color> REGISTRY = new HashMap<>();

    public static final Color WHITE = register("white", 0f, 0f, 1f);
    public static final Color RED = register("red", 0f, 1f, 1f);
    public static final Color ORANGE = register("orange", 0.06f, 0.88f, 1f);
    public static final Color YELLOW = register("yellow", 0.17f, 1f, 1f);
    public static final Color LIME = register("lime", 0.2f, 1f, 1f);
    public static final Color GREEN = register("green", 0.33f, 1f, 1f);
    public static final Color CYAN = register("cyan", 0.5f, 1f, 1f);
    public static final Color LIGHT_BLUE = register("light_blue", 0.54f, 0.25f, 1f);
    public static final Color BLUE = register("blue", 0.67f, 1f, 1f);
    public static final Color PURPLE = register("purple", 0.77f, 0.87f, 1f);
    public static final Color MAGENTA = register("magenta", 0.83f, 1f, 1f);
    public static final Color PINK = register("pink", 0.92f, 0.59f, 1f);

    public static final Color OFF_WHITE = registerOff("off_white", WHITE);
    public static final Color OFF_RED = registerOff("off_red", RED);
    public static final Color OFF_ORANGE = registerOff("off_orange", ORANGE);
    public static final Color OFF_YELLOW = registerOff("off_yellow", YELLOW);
    public static final Color OFF_LIME = registerOff("off_lime", LIME);
    public static final Color OFF_GREEN = registerOff("off_green", GREEN);
    public static final Color OFF_CYAN = registerOff("off_cyan", CYAN);
    public static final Color OFF_LIGHT_BLUE = registerOff("off_light_blue", LIGHT_BLUE);
    public static final Color OFF_BLUE = registerOff("off_blue", BLUE);
    public static final Color OFF_PURPLE = registerOff("off_purple", PURPLE);
    public static final Color OFF_MAGENTA = registerOff("off_magenta", MAGENTA);
    public static final Color OFF_PINK = registerOff("off_pink", PINK);

    public Color(float h, float s, float b) {
      this.h = h;
      this.s = s;
      this.b = b;

      if (h > 1 || h < 0) {
        throw new AssertionError("Hue must be in the range [0,1]");
      }

      if (s > 1 || s < 0) {
        throw new AssertionError("Saturation must be in the range [0,1]");
      }

      if (b > 1 || b < 0) {
        throw new AssertionError("Brightness must be in the range [0,1]");
      }
    }

    private static Color register(String name, float h, float s, float b) {
      if (REGISTRY.containsKey(name))
        throw new RuntimeException("Already registered a color with name " + name);

      Color color = new Color(h, s, b);
      REGISTRY.put(name, color);
      return color;
    }

    private static Color registerOff(String name, Color on) {
      return register(name, on.h, on.s, 0.1f);
    }

    public Color h(float h) {
      return new Color(h, s, b);
    }

    public Color s(float s) {
      return new Color(h, s, b);
    }

    public Color b(float b) {
      return new Color(h, s, b);
    }

    // https://axonflux.com/handy-rgb-to-hsl-and-rgb-to-hsv-color-model-c
    public float[] toRGB() {
      int i = Mth.floor(h * 6);
      float f = h * 6 - i;
      float p = b * (1 - s);
      float q = b * (1 - f * s);
      float t = b * (1 - (1 - f) * s);

      switch (i % 6) {
        case 0: return new float[]{b, t, p};
        case 1: return new float[]{q, b, p};
        case 2: return new float[]{p, b, t};
        case 3: return new float[]{p, q, b};
        case 4: return new float[]{t, p, b};
        case 5: return new float[]{b, p, q};
      }

      throw new RuntimeException("laws of mathematics broken");
    }

    @Override
    public String toString() {
      return "hsb(" + h + ", " + s + ", " + b + ")";
    }
  }
}
