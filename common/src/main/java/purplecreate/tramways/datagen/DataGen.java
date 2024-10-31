package purplecreate.tramways.datagen;

import com.tterrag.registrate.providers.ProviderType;
import purplecreate.tramways.Tramways;

public class DataGen {
  public static void register() {
    Tramways.REGISTRATE.addDataGenerator(ProviderType.LANG, GenLang::generator);
    Tramways.REGISTRATE.addDataGenerator(ProviderType.RECIPE, GenRecipes::generator);
  }
}
