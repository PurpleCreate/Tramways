package purplecreate.tramways.content.signals.render;

import com.google.gson.JsonObject;
import net.createmod.catnip.animation.LerpedFloat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import purplecreate.tramways.content.signals.base.ExtendedSignalState;
import purplecreate.tramways.content.signals.base.JunctionState;
import purplecreate.tramways.content.signals.models.AspectModel;

@Environment(EnvType.CLIENT)
public class SquareAspect extends Aspect {
  private float radius;
  private ConfigFunction<Color> colorFunction;

  private final LerpedFloat red = LerpedFloat.linear();
  private final LerpedFloat green = LerpedFloat.linear();
  private final LerpedFloat blue = LerpedFloat.linear();
  private final LerpedFloat brightness = LerpedFloat.linear();

  @Override
  public Aspect copy() {
    SquareAspect aspect = new SquareAspect();
    aspect.type = type;
    aspect.center = center;
    aspect.radius = radius;
    aspect.colorFunction = colorFunction;
    return aspect;
  }

  @Override
  public void tick(ExtendedSignalState signal, JunctionState junction) {
    Color color = colorFunction.execute(signal, junction);
    if (color == null) return;

    float[] rgb = color.b(Math.max(0.1f, color.b())).toRGB();

    red.chaseTimed(rgb[0], 1);
    green.chaseTimed(rgb[1], 1);
    blue.chaseTimed(rgb[2], 1);
    brightness.chaseTimed(color.b(), 1);

    red.tickChaser();
    green.tickChaser();
    blue.tickChaser();
    brightness.tickChaser();
  }

  @Override
  public void render(Renderer r, float partialTicks, int light, int overlay) {
    r.ms.pushPose();
    r.ms.translate(
      1 + center.x / -16.0,
      1 + center.y / -16.0,
      (16 - center.z) / 16f - 0.001
    );

    float cb = brightness.getValue(partialTicks);
    int sky = LightTexture.sky(light);
    int block = LightTexture.block(light);
    sky = Mth.lerpInt(cb, sky, LightTexture.FULL_SKY);
    block = Mth.lerpInt(cb, block, LightTexture.FULL_BLOCK);

    r.renderModel(
      AspectModel.create(radius),
      RenderType.solid(),
      LightTexture.pack(block, sky),
      overlay,
      red.getValue(partialTicks),
      green.getValue(partialTicks),
      blue.getValue(partialTicks)
    );

    r.ms.popPose();
  }

  @Override
  protected void deserialize(JsonObject obj) {
    radius = obj.get("radius").getAsFloat();
    colorFunction = parseFunction(obj.get("color").getAsString(), Color.class);
  }
}
