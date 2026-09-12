package purplecreate.tramways.content.signals.render;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.simibubi.create.AllShapes;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.math.AngleHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import purplecreate.tramways.TBlocks;
import purplecreate.tramways.content.signals.block.GenericSignalBlock;
import purplecreate.tramways.content.signals.block.GenericSignalBlockEntity;
import purplecreate.tramways.util.CodecUtil;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class SignalType {
  private final ResourceLocation id;
  private final ResourceLocation group;

  protected ResourceLocation model;
  private ModelType modelType;
  private BindingType bindingType;
  private Vec3 fromBound;
  private Vec3 toBound;
  private List<Aspect> aspects;
  private List<String> itemAnim;

  protected BakedModel bakedModel = null;

  private SignalType(ResourceLocation id, ResourceLocation group) {
    this.id = id;
    this.group = group;
  }

  public static SignalType fromJson(ResourceLocation id, ResourceLocation group, JsonElement json) {
    JsonObject obj = json.getAsJsonObject();
    SignalType result = new SignalType(id, group);

    result.model = CodecUtil.fromJson(ResourceLocation.CODEC, obj.get("model"));
    result.modelType = CodecUtil.fromJson(ModelType.CODEC, obj.get("model_type"));
    result.bindingType = CodecUtil.fromJson(BindingType.CODEC, obj.get("binding_type"));
    result.fromBound = CodecUtil.fromJson(Vec3.CODEC, obj.get("from_bound"));
    result.toBound = CodecUtil.fromJson(Vec3.CODEC, obj.get("to_bound"));
    result.aspects = new ArrayList<>();
    for (JsonElement e : obj.getAsJsonArray("aspects")) {
      result.aspects.add(Aspect.fromJson(e));
    }
    result.itemAnim = new ArrayList<>();
    for (JsonElement e : obj.getAsJsonArray("item_anim")) {
      result.itemAnim.add(e.getAsString());
    }

    return result;
  }

  public SignalType copy() {
    SignalType copy = new SignalType(id, group);
    copy.model = model;
    copy.modelType = modelType;
    copy.bindingType = bindingType;
    copy.fromBound = fromBound;
    copy.toBound = toBound;
    copy.aspects = aspects.stream().map(Aspect::copy).toList();
    copy.itemAnim = new ArrayList<>(itemAnim);

    copy.bakedModel = bakedModel;

    return copy;
  }

  public void tick(GenericSignalBlockEntity be) {
    for (Aspect aspect : aspects) {
      aspect.tick(be.getSignalState(), be.getJunctionState());
    }
  }

  public void render(Renderer r, float partialTicks, int light, int overlay) {
    var msr = TransformStack.of(r.ms);
    BlockState state = r instanceof Renderer.Block br
      ? br.be.getBlockState()
      : TBlocks.GENERIC_SIGNAL.getDefaultState();

    msr
      .pushPose()
      .translate(TBlocks.GENERIC_SIGNAL.get().getRenderOffset(state))
      .center()
      .rotateYDegrees(AngleHelper.horizontalAngle(
        state.getValue(GenericSignalBlock.FACING).getOpposite()
      ))
      .uncenter();

    r.renderModel(bakedModel, RenderType.cutout(), light, overlay);

    msr.pushPose();

    for (Aspect aspect : aspects) {
      aspect.render(r, partialTicks, light, overlay);
    }

    msr
      .popPose()
      .popPose();
  }

  public ResourceLocation getId() {
    return id;
  }

  public VoxelShape getShape(Direction direction) {
    return new AllShapes.Builder(Block.box(fromBound.x, fromBound.y, fromBound.z, toBound.x, toBound.y, toBound.z))
      .forHorizontal(Direction.NORTH)
      .get(direction);
  }

  public enum ModelType implements StringRepresentable {
    BLOCK("block"),
    GIRDER("girder");

    public static final EnumCodec<ModelType> CODEC = StringRepresentable.fromEnum(ModelType::values);
    private final String name;

    ModelType(String name) {
      this.name = name;
    }

    @Override
    public String getSerializedName() {
      return name;
    }
  }

  public enum BindingType implements StringRepresentable {
    SIGNAL("signal"),
    TRACK("track");

    public static final EnumCodec<BindingType> CODEC = StringRepresentable.fromEnum(BindingType::values);
    private final String name;

    BindingType(String name) {
      this.name = name;
    }

    @Override
    public String getSerializedName() {
      return name;
    }
  }
}
