package io.github.brenoepics.translation

import ch.qos.logback.classic.Level
import io.github.brenoepics.api.APIProviders
import org.ini4j.Ini
import translation.TranslationManager
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import java.util.stream.Collectors

/**
 * Configuration class for the translation process.
 * @param ini The ini file with the configuration.
 */
class TranslationConfig(ini: Ini) {
    val loggerLevel: Level = Level.valueOf(ini["logger", "level"] ?: "INFO")
    val recursiveInput = ini["files", "recursive"]?.toBoolean() ?: false
    val indentationLevel = ini["files", "indentation"]?.toInt() ?: 2
    val sourcePath: Path
    val targetPath: Path
    val outputTemplate: String = ini["files", "output_template"] ?: "{input_dir}/{to_lang}/{input_filename}.{input_extension}"
    private val provider: String
    private val languages: Array<String>
    val from: String
    val to: Set<String>
    private val translationManager: TranslationManager
    val api: Translator?

    init {
        try {

            sourcePath = Paths.get(ini["files", "input"] ?: throw TranslationException("Missing input file path"))
            targetPath = Paths.get(ini["files", "output"] ?: throw TranslationException("Missing output file path"))
            provider = ini["api", "provider"] ?: throw TranslationException("Missing API provider")
            languages = ini["translate", "to"]?.split(";".toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray()
                ?: throw TranslationException("Missing languages to translate to")
            from = ini["translate", "from"] ?: throw TranslationException("Missing language to translate from")
            to = Arrays.stream(languages).collect(Collectors.toSet())
            translationManager = TranslationManager(ini)
            api = getApi(translationManager, provider)
        } catch (e: Exception) {
            throw TranslationException("Error initializing TranslationConfig", e)
        }
    }

    private fun getApi(
        translationManager: TranslationManager, provider: String
    ): Translator? = translationManager.getAPI(
        APIProviders.fromString(provider)
    ).build()
}

class TranslationException(message: String, cause: Throwable? = null) : Exception(message, cause)