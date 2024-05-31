package api.builder;

import io.github.brenoepics.api.AzureTranslator;
import io.github.brenoepics.translation.Translator;
import org.ini4j.Ini;

public class AzureBuilder extends ApiBuilder {
		private final String apiKey;
		private final String region;

		public AzureBuilder(Ini ini) {
				super(ini);
				apiKey = ini.get("azure", "key");
				region = ini.get("azure", "region");
		}

		@Override
		public Translator build() {
				return new AzureTranslator(apiKey, region);
		}
}
