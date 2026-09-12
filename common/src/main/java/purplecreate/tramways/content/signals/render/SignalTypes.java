package purplecreate.tramways.content.signals.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import purplecreate.tramways.Tramways;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static purplecreate.tramways.content.signals.block.GenericSignalBlockEntity.DEFAULT_SIGNAL_TYPE;

@Environment(EnvType.CLIENT)
public class SignalTypes {
  private static final String DIRECTORY = Tramways.ID + "_signals";
  private static final Map<ResourceLocation, SignalType> SIGNAL_TYPES = new HashMap<>();

  public static SignalType getDefault() {
    return get(DEFAULT_SIGNAL_TYPE);
  }

  public static SignalType get(ResourceLocation id) {
    return SIGNAL_TYPES.get(id).copy();
  }

  public static void init(Consumer<ResourceLocation> requestModel) {
    ResourceManager manager = Minecraft.getInstance().getResourceManager();
    SIGNAL_TYPES.clear();

    AtomicInteger successfulLoads = new AtomicInteger();

    FileToIdConverter.json(DIRECTORY)
      .listMatchingResources(manager)
      .forEach((location, resource) -> {
        String namespace = location.getNamespace();
        String path = location.getPath();
        int lastSlash = path.lastIndexOf('/');
        int extension = path.lastIndexOf(".json");

        ResourceLocation id = new ResourceLocation(namespace, path.substring(lastSlash + 1, extension));
        ResourceLocation group = new ResourceLocation(namespace, path.substring(0, lastSlash));

        try (BufferedReader reader = resource.openAsReader()) {
          if (SIGNAL_TYPES.containsKey(id)) {
            throw new AssertionError("Duplicate key");
          }

          SignalType type = SignalType.fromJson(id, group, GsonHelper.parse(reader));

          requestModel.accept(type.model);

          SIGNAL_TYPES.put(id, type);
          successfulLoads.getAndIncrement();
        } catch (Exception e) {
          throw new RuntimeException("Could not load signal type with id " + id, e);
        }
      });

    Tramways.LOGGER.info("Successfully loaded {} signal types", successfulLoads.get());
  }

  public static void onBaked(Map<ResourceLocation, BakedModel> bakedModels) {
    for (SignalType type : SIGNAL_TYPES.values()) {
      type.bakedModel = bakedModels.get(type.model);
    }
  }
}
