package io.github.brenoepics.translation

import java.util.concurrent.CompletableFuture

interface Translator {

    fun getCache(): HashMap<String, HashMap<String, String>>
    fun addToCache(lang: String, translations: HashMap<String, String>)
    fun translate(
        texts: Map<String, String>,
        source: String,
        target: Set<String>
    )
    fun dispose()
}