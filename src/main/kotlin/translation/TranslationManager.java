package translation;

import api.builder.ApiBuilder;
import api.builder.AzureBuilder;
import io.github.brenoepics.api.APIProviders;
import org.ini4j.Ini;

import java.util.EnumMap;

/**
 * This class is responsible for managing the translation API's.
 * It creates the API's and stores them in a EnumMap.
 */
public class TranslationManager {

		private final EnumMap<APIProviders, ApiBuilder> translators = new EnumMap<>(APIProviders.class);

		public TranslationManager(Ini ini) {
				createTranslators(ini);
		}

		private void createTranslators(Ini ini) {
				translators.put(APIProviders.AZURE, new AzureBuilder(ini));
		}

		public ApiBuilder getAPI(APIProviders provider) {
				return translators.get(provider);
		}
}
