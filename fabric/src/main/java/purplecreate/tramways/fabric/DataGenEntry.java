package purplecreate.tramways.fabric;

import io.github.fabricators_of_create.porting_lib.data.ExistingFileHelper;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import purplecreate.tramways.Tramways;
import purplecreate.tramways.datagen.DataGen;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

public class DataGenEntry implements DataGeneratorEntrypoint {
  @Override
  public void onInitializeDataGenerator(FabricDataGenerator gen) {
    Path resources = Paths.get(System.getProperty(ExistingFileHelper.EXISTING_RESOURCES));
    ExistingFileHelper helper = new ExistingFileHelper(
      Set.of(resources), Set.of("create"), false, null, null
    );
    Tramways.REGISTRATE.setupDatagen(gen.createPack(), helper);
    DataGen.register();
  }
}
