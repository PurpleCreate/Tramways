package purplecreate.tramways.datagen;

import com.simibubi.create.foundation.block.DyedBlockList;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.Display.TextDisplay.Align;
import net.minecraft.world.item.DyeColor;
import purplecreate.tramways.TBlocks;
import purplecreate.tramways.content.stationDeco.nameSign.NameSignBlock;
import purplecreate.tramways.content.stationDeco.nameSign.info.NameSignInfoProvider;

public class GenNameSignInfo extends NameSignInfoProvider {
  public GenNameSignInfo(PackOutput output) {
    super(output);
  }

  @Override
  protected void createData() {
    DyedBlockList<NameSignBlock> map = TBlocks.STATION_NAME_SIGNS;

    builder()
      .align(Align.LEFT)
      .offset(5)
      .width(60)
      .color(DyeColor.WHITE)
      .register(
        map.get(DyeColor.BLUE),
        map.get(DyeColor.MAGENTA),
        map.get(DyeColor.ORANGE),
        map.get(DyeColor.CYAN),
        map.get(DyeColor.PURPLE)
      );

    builder()
      .align(Align.LEFT)
      .offset(5)
      .width(60)
      .register(map.get(DyeColor.YELLOW));

    builder()
      .color(DyeColor.WHITE)
      .register(
        map.get(DyeColor.GRAY),
        map.get(DyeColor.BLACK),
        map.get(DyeColor.BROWN)
      );

    builder()
      .register(
        map.get(DyeColor.GREEN),
        map.get(DyeColor.LIGHT_BLUE),
        map.get(DyeColor.LIME),
        map.get(DyeColor.PINK),
        map.get(DyeColor.RED),
        map.get(DyeColor.WHITE),
        map.get(DyeColor.LIGHT_GRAY)
      );
  }
}
