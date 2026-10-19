package purplecreate.tramways.datagen;

import com.tterrag.registrate.providers.ProviderType;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import purplecreate.tramways.Tramways;
import purplecreate.tramways.content.announcements.config.AnnouncementEvent;

import java.util.function.Function;

@EventBusSubscriber
public class DataGen {
  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public static void onGatherDataHigh(GatherDataEvent event) {
    AnnouncementEvent.registerLang();

    Tramways.REGISTRATE.addDataGenerator(ProviderType.LANG, GenLang::generator);
    Tramways.REGISTRATE.addDataGenerator(ProviderType.RECIPE, GenRecipes::generator);
  }

  @SubscribeEvent(priority = EventPriority.LOWEST)
  public static void onGatherDataLow(GatherDataEvent event) {
    DataGenerator generator = event.getGenerator();
    PackOutput output = generator.getPackOutput();

    generator.addProvider(event.includeClient(), new GenNameSignInfo(output));
  }
}
