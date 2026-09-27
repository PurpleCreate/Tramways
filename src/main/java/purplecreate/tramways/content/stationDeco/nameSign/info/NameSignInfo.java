package purplecreate.tramways.content.stationDeco.nameSign.info;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Display.TextDisplay.Align;
import net.minecraft.world.item.DyeColor;
import purplecreate.tramways.Tramways;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;

public class NameSignInfo implements ResourceManagerReloadListener {
  private static final Map<ResourceLocation, Entry> infoMap = new HashMap<>();
  public static final String infoDirectory = "tramways_name_sign";
  public static final NameSignInfo listener = new NameSignInfo();

  public void onResourceManagerReload(ResourceManager manager) {
    infoMap.clear();

    FileToIdConverter.json(infoDirectory)
      .listMatchingResources(manager)
      .forEach((location, resource) -> {
        String[] pathParts = location.getPath().split("/");
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(
          location.getNamespace(),
          pathParts[pathParts.length - 1].replace(".json", "")
        );

        try (BufferedReader reader = resource.openAsReader()) {
          JsonObject obj = GsonHelper.parse(reader);
          infoMap.put(
            id,
            Entry.CODEC.parse(JsonOps.INSTANCE, obj)
              .resultOrPartial(Tramways.LOGGER::error)
              .orElseThrow()
          );

          Tramways.LOGGER.info("Loaded name sign info for {}", id);
        } catch (Exception e) {
          Tramways.LOGGER.error("Couldn't load name sign info for {}", id, e);
        }
      });
  }

  public static Entry get(ResourceLocation location) {
    return infoMap.getOrDefault(location, Entry.DEFAULT);
  }

  public record Entry(Align align, int offset, DyeColor color) {
    public static final Entry DEFAULT = new Entry(Align.CENTER, 0, DyeColor.BLACK);
    public static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance ->
      instance.group(
        Align.CODEC.fieldOf("align").forGetter(Entry::align),
        Codec.INT.fieldOf("offset").forGetter(Entry::offset),
        DyeColor.CODEC.fieldOf("color").forGetter(Entry::color)
      ).apply(instance, Entry::new)
    );

    public Entry forceCentered() {
      return new Entry(
        Align.CENTER,
        this.offset,
        this.color
      );
    }

    public Entry forceCenteredIf(boolean value) {
      return value ? forceCentered() : this;
    }
  }
}
