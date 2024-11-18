package purplecreate.tramways.content.stationDeco.nameSign.info;

import com.mojang.serialization.JsonOps;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Display.TextDisplay.Align;
import net.minecraft.world.item.DyeColor;
import purplecreate.tramways.Tramways;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class NameSignInfoProvider implements DataProvider {
  private final PackOutput.PathProvider path;
  private final Map<ResourceLocation, NameSignInfo.Entry> toRegister = new HashMap<>();

  public NameSignInfoProvider(PackOutput output) {
    path = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, NameSignInfo.infoDirectory);
  }

  protected abstract void createData();

  public Builder builder() {
    return new Builder();
  }

  public void add(BlockEntry<?> blockEntry, NameSignInfo.Entry entry) {
    toRegister.put(blockEntry.getId(), entry);
  }

  public void add(ResourceLocation location, NameSignInfo.Entry entry) {
    toRegister.put(location, entry);
  }

  @Override
  public CompletableFuture<?> run(CachedOutput output) {
    toRegister.clear();
    createData();
    return CompletableFuture.allOf(
      toRegister.entrySet().stream().map(entry ->
        DataProvider.saveStable(
          output,
          NameSignInfo.Entry.CODEC.encodeStart(JsonOps.INSTANCE, entry.getValue())
            .resultOrPartial(Tramways.LOGGER::error)
            .orElseThrow(),
          path.json(entry.getKey())
        )
      ).toArray(CompletableFuture[]::new)
    );
  }

  @Override
  public String getName() {
    return "Tramways: Name Sign Info";
  }

  public class Builder {
    private Align align = NameSignInfo.Entry.DEFAULT.align();
    private int offset = NameSignInfo.Entry.DEFAULT.offset();
    private int width = NameSignInfo.Entry.DEFAULT.width();
    private DyeColor color = NameSignInfo.Entry.DEFAULT.color();

    public Builder align(Align align) {
      this.align = align;
      return this;
    }

    public Builder offset(int offset) {
      this.offset = offset;
      return this;
    }

    public Builder width(int width) {
      this.width = width;
      return this;
    }

    public Builder color(DyeColor color) {
      this.color = color;
      return this;
    }

    public NameSignInfo.Entry build() {
      return new NameSignInfo.Entry(align, offset, width, color);
    }

    public void register(BlockEntry<?>... blockEntries) {
      for (BlockEntry<?> blockEntry : blockEntries)
        add(blockEntry, build());
    }

    public void register(ResourceLocation... locations) {
      for (ResourceLocation location : locations)
        add(location, build());
    }
  }
}
