package io.github.brenoepics.api

import java.util.*

/**
 * Enum class for the API providers.
 * @property AZURE Azure Translator API.
 * @property GOOGLE Google Translate API.
 * @property DEEPL DeepL API.
 * @constructor Creates an API provider from a string.
 * TODO: Add Google and DeepL API classes.
 */
enum class APIProviders {
    AZURE, GOOGLE, DEEPL;

    companion object {
        fun fromString(name: String): APIProviders {
            return when (name.lowercase(Locale.getDefault())) {
                "azure" -> AZURE
                "google" -> GOOGLE
                "deepl" -> DEEPL
                else -> throw IllegalArgumentException("Invalid provider name")
            }
        }
    }
}