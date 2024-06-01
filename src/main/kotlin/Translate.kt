package io.github.brenoepics

import ch.qos.logback.classic.Logger
import io.github.brenoepics.translation.TranslationConfig
import io.github.brenoepics.utils.FileManager.Companion.readIni
import org.slf4j.LoggerFactory
import utils.TranslateUtils

/**
 * Main function to start the translation process.
 * Reads the ini file and starts the translation process.
 * @see TranslationConfig
 * @see readIni
 * @see TranslateUtils.translateJSON
 */
fun main() {
    val ini = readIni()
    val config = TranslationConfig(ini)
    val root = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger
    root.level = config.loggerLevel
    TranslateUtils.translateJSON(config, HashMap())
}

