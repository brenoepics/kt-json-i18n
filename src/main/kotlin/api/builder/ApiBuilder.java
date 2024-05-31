package api.builder;

import io.github.brenoepics.translation.Translator;
import org.ini4j.Ini;
import translation.TranslationException;

public abstract class ApiBuilder {
		ApiBuilder(Ini ini) {
		}

		public Translator build() {
				throw new TranslationException("Not implemented");
		}
}
