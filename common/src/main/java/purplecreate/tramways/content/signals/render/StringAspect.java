package purplecreate.tramways.content.signals.render;

import com.google.gson.JsonObject;
import net.createmod.catnip.animation.LerpedFloat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.util.Mth;
import purplecreate.tramways.Tramways;
import purplecreate.tramways.content.signals.base.ExtendedSignalState;
import purplecreate.tramways.content.signals.base.JunctionState;

@Environment(EnvType.CLIENT)
public class StringAspect extends Aspect {
  private float scale;
  private ConfigFunction<Color> colorFunction;
  private ConfigFunction<String> contentFunction;

  private final LerpedFloat red = LerpedFloat.linear();
  private final LerpedFloat green = LerpedFloat.linear();
  private final LerpedFloat blue = LerpedFloat.linear();
  private final LerpedFloat brightness = LerpedFloat.linear();
  private String lastNonNull = "";

  @Override
  public Aspect copy() {
    StringAspect aspect = new StringAspect();
    aspect.type = type;
    aspect.center = center;
    aspect.scale = scale;
    aspect.colorFunction = colorFunction;
    aspect.contentFunction = contentFunction;
    return aspect;
  }

  @Override
  public void tick(ExtendedSignalState signal, JunctionState junction) {
    Color color = colorFunction.execute(signal, junction);
    String content = contentFunction.execute(signal, junction);

    Tramways.LOGGER.info(content);

    if (color == null) return;

    if (content != null) {
      lastNonNull = content;
    }

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
    Font font = Minecraft.getInstance().font;

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

    int re = (int)(red.getValue(partialTicks) * 255);
    int gr = (int)(green.getValue(partialTicks) * 255);
    int bl = (int)(blue.getValue(partialTicks) * 255);
    int color = (re << 16) | (gr << 8) | bl;

    if (cb > 0) {
      r.renderText(
        font,
        lastNonNull,
        font.width(lastNonNull) / -2f,
        font.lineHeight / -2f,
        scale,
        color,
        0,
        LightTexture.pack(block, sky)
      );
    }

    r.ms.popPose();
  }

  @Override
  protected void deserialize(JsonObject obj) {
    scale = obj.get("scale").getAsFloat();
    colorFunction = parseFunction(obj.get("color").getAsString(), Color.class);
    contentFunction = parseFunction(obj.get("content").getAsString(), String.class);
  }
}
