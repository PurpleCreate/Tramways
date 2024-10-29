package purplecreate.tramways.datagen;

import com.google.gson.JsonElement;
import com.simibubi.create.foundation.ponder.PonderLocalization;
import com.simibubi.create.foundation.utility.FilesHelper;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import purplecreate.tramways.Tramways;

import java.util.Map;

public class GenLang {
  public static void generator(RegistrateLangProvider provider) {
    PonderLocalization.generateSceneLang();
    PonderLocalization.provideLang(Tramways.ID, provider::add);

    // defaults
    JsonElement elem = FilesHelper.loadJsonResource("assets/tramways/lang/defaults.json");

    if (elem == null) {
      throw new RuntimeException("Couldn't find lang/defaults.json");
    }

    for (Map.Entry<String, JsonElement> entry : elem.getAsJsonObject().entrySet()) {
      provider.add(entry.getKey(), entry.getValue().getAsString());
    }
  }
}
