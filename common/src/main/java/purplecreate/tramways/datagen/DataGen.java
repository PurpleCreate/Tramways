package purplecreate.tramways.datagen;

import com.tterrag.registrate.providers.ProviderType;
import net.minecraft.data.DataProvider;
import purplecreate.tramways.Tramways;

import java.util.function.Function;

public class DataGen {
  public static void register() {
    Tramways.REGISTRATE.addDataGenerator(ProviderType.LANG, GenLang::generator);
    Tramways.REGISTRATE.addDataGenerator(ProviderType.RECIPE, GenRecipes::generator);
  }

  public static void registerNonRegistrate(Function<DataProvider.Factory<DataProvider>, DataProvider> addProvider) {
    addProvider.apply(GenNameSignInfo::new);
  }
}
