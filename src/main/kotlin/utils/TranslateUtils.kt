package utils

import io.github.brenoepics.translation.TranslationConfig
import io.github.brenoepics.translation.Translator
import io.github.brenoepics.utils.FileManager
import io.github.brenoepics.utils.FileManager.Companion.getJsonTexts
import io.github.brenoepics.utils.FileManager.Companion.readJsonFile
import io.github.brenoepics.utils.FileManager.Companion.writeTranslation
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Path

/**
 * Utility class for translating JSON files.
 */
class TranslateUtils {
    companion object {

        /**
         * Translates the JSON files in the source path to the target path.
         * @param config The configuration for the translation process.
         * @param toTranslate The texts to translate.
         */
        fun translateJSON(
            config: TranslationConfig,
            toTranslate: HashMap<String, String>,
        ) {
            val dirFiles = FileManager.readPath(config.sourcePath, config.recursiveInput)
            val filesData: HashMap<Path, Any?> = HashMap()
            log.info("Reading files...")
            readFiles(dirFiles, toTranslate, filesData)
            config.api?.translate(toTranslate, config.from, config.to)
            config.api?.let { updateFileData(config, dirFiles, filesData, it) }
            log.info("Translation finished!")
            config.api?.dispose()
        }

        private fun readFiles(
            dirFiles: List<Path>,
            toTranslate: HashMap<String, String>,
            filesData: HashMap<Path, Any?>
        ) {
            for (it in dirFiles) {
                val file = readJsonFile(it)
                toTranslate.putAll(getJsonTexts(file))
                filesData[it] = file
                log.debug("Read file: {}", it)
            }
        }

        private fun updateFileData(
            config: TranslationConfig, dirFiles: List<Path>, filesData: HashMap<Path, Any?>, api: Translator
        ) {
            log.info("Generating translated files...")
            dirFiles.forEach {
                val json = filesData[it]
                if (json != null) {
                    for (lang in config.to) {
                        val translations = api.getLanguageCache(lang)
                        translations.writeTranslation(
                            config.outputTemplate,
                            config.sourcePath,
                            json,
                            config.targetPath,
                            it,
                            lang,
                            config.indentationLevel
                        )
                    }
                }
            }
        }

        private val log: Logger = LoggerFactory.getLogger(TranslateUtils::class.java)
    }
}
