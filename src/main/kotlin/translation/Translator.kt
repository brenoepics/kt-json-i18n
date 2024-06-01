package io.github.brenoepics.translation

interface Translator {
    fun getLanguageCache(langCode: String): java.util.HashMap<String, String>
    fun addToCache(lang: String, translations: HashMap<String, String>)
    fun translate(
        texts: Map<String, String>,
        source: String,
        target: Set<String>
    )
    fun dispose()
}