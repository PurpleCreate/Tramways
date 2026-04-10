package purplecreate.tramways.content.signals.base;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import purplecreate.tramways.content.signals.models.AspectModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public abstract class Aspect<T> {
  protected final float x;
  protected final float y;
  protected final int color;
  protected final Function<StateHolder, T> stateGetter;

  protected float lastRenderTime = 0;
  protected T currentState = null;
  protected T lastState = null;
  protected float progress = 0;

  protected Aspect(float x, float y, int color, Function<StateHolder, T> stateGetter) {
    this.x = x;
    this.y = y;
    this.color = color;
    this.stateGetter = stateGetter;
  }

  protected Aspect(float x, float y, DyeColor color, Function<StateHolder, T> stateGetter) {
    this(x, y, color.getTextColor(), stateGetter);
  }

  protected float timing(float progress) {
    return (float) Math.sqrt(1 - Math.pow(progress - 1, 2));
  }

  protected int lerpColorChannel(float mod, int c) {
    return Mth.lerpInt(mod, Mth.floor(c * 0.1), c);
  }

  protected int getLight(int light, boolean on) {
    return on ? LightTexture.FULL_BRIGHT : light;
  }

  abstract protected void onRender(StateHolder state, SignalType signalType, PoseStack ms, MultiBufferSource buffer, float renderTime, int light, int overlay);

  protected void onStateChange(T newState, T oldState) {
  }

  public void render(StateHolder signal, SignalType signalType, PoseStack ms, MultiBufferSource buffer, float renderTime, int light, int overlay) {
    currentState = stateGetter.apply(signal);

    float delta = renderTime - lastRenderTime;
    lastRenderTime = renderTime;

    if (!Objects.equals(lastState, currentState)) {
      onStateChange(currentState, lastState);
      lastState = currentState;
      progress = 0;
    }
    progress = Math.min(progress + (delta / 5), 1);

    onRender(signal, signalType, ms, buffer, renderTime, light, overlay);
  }

  private static class SimpleAspect extends Aspect<Boolean> {
    private final float radius;

    public SimpleAspect(float x, float y, float radius, int color, Function<StateHolder, Boolean> active) {
      super(x, y, color, active);
      this.radius = radius;
    }

    public SimpleAspect(float x, float y, float radius, DyeColor color, Function<StateHolder, Boolean> active) {
      this(x, y, radius, color.getTextColor(), active);
    }

    @Override
    protected void onRender(StateHolder signal, SignalType signalType, PoseStack ms, MultiBufferSource buffer, float renderTime, int light, int overlay) {
      float mod = timing(progress);
      if (!currentState) mod = 1 - mod;

      int r = lerpColorChannel(mod, (color >> 16) & 255);
      int g = lerpColorChannel(mod, (color >> 8) & 255);
      int b = lerpColorChannel(mod, color & 255);

      ms.pushPose();
      ms.translate(1 + x / -16f, 1 + y / -16f, (16 - signalType.getSignalFaceZ()) / 16f - 0.001);

      signal.render(
        AspectModel.create(radius), ms, buffer.getBuffer(RenderType.solid()),
        getLight(light, mod > 0.5f), overlay, r, g, b
      );

      ms.popPose();
    }
  }

  private static class StringAspect extends Aspect<String> {
    private final float scale;
    private String text;

    public StringAspect(float centerX, float centerY, float scale, int color, Function<StateHolder, String> string) {
      super(centerX, centerY, color, string);
      this.scale = scale / 16;
    }

    public StringAspect(float centerX, float centerY, float scale, DyeColor color, Function<StateHolder, String> string) {
      this(centerX, centerY, scale, color.getTextColor(), string);
    }

    @Override
    protected void onStateChange(String newState, String oldState) {
      if (newState != null) text = newState;
    }

    @Override
    protected void onRender(StateHolder signal, SignalType signalType, PoseStack ms, MultiBufferSource buffer, float renderTime, int light, int overlay) {
      float mod = timing(progress);

      if (currentState == null) mod = 1 - mod;
      if (text == null) return;

      Font renderer = Minecraft.getInstance().font;

      int r = lerpColorChannel(mod, (color >> 16) & 255);
      int g = lerpColorChannel(mod, (color >> 8) & 255);
      int b = lerpColorChannel(mod, color & 255);

      float width = renderer.getSplitter().stringWidth(text);
      int packedColor = (r << 16) | (g << 8) | b;

      ms.pushPose();
      ms.translate(1 + x / -16f, 1 + y / -16f, (16 - signalType.getSignalFaceZ()) / 16f - 0.001);
      ms.scale(-scale, -scale, scale);
      ms.translate(0.5, 0.5, 0);

      renderer.drawInBatch(
        text, width / -2f, renderer.lineHeight / -2f, packedColor, false,
        ms.last().pose(), buffer, Font.DisplayMode.NORMAL, 0, getLight(light, mod > 0.5f)
      );

      ms.popPose();
    }
  }

  public static class Builder {
    private final List<Aspect<?>> aspects = new ArrayList<>();
    private boolean built = false;

    public Builder addSimple(float x, float y, float radius, int color, Function<StateHolder, Boolean> active) {
      aspects.add(new SimpleAspect(x, y, radius, color, active));
      return this;
    }

    public Builder addSimple(float x, float y, float radius, DyeColor color, Function<StateHolder, Boolean> active) {
      aspects.add(new SimpleAspect(x, y, radius, color, active));
      return this;
    }

    public Builder addString(float centerX, float centerY, float scale, int color, Function<StateHolder, String> string) {
      aspects.add(new StringAspect(centerX, centerY, scale, color, string));
      return this;
    }

    public Builder addString(float centerX, float centerY, float scale, DyeColor color, Function<StateHolder, String> string) {
      aspects.add(new StringAspect(centerX, centerY, scale, color, string));
      return this;
    }

    public List<Aspect<?>> build() {
      if (built) throw new IllegalStateException("Already built");
      built = true;
      return aspects;
    }
  }
}
