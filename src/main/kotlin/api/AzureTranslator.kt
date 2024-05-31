package io.github.brenoepics.api

import io.github.brenoepics.at4j.AzureApi
import io.github.brenoepics.at4j.AzureApiBuilder
import io.github.brenoepics.at4j.core.thread.AT4JThreadFactory
import io.github.brenoepics.at4j.data.TranslationResult
import io.github.brenoepics.at4j.data.request.TranslateParams
import io.github.brenoepics.at4j.data.response.TranslationResponse
import io.github.brenoepics.translation.Translator
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.*

/**
 * Translator implementation for Azure Translator API.
 */
class AzureTranslator(
    apiKey: String, region: String
) : Translator {
    private var azureApi: AzureApi =
        AzureApiBuilder().setKey(apiKey).region(region).executorService(newAt4jDefault()).build()
    private val cache = HashMap<String, HashMap<String, String>>()

    companion object {
        const val MAX_CHARACTERS = 5000
        val log: Logger = LoggerFactory.getLogger(AzureTranslator::class.java)
        fun newAt4jDefault(): ExecutorService {
            return ThreadPoolExecutor(
                1, Int.MAX_VALUE, 60L, TimeUnit.SECONDS, SynchronousQueue(), AT4JThreadFactory("AT4J - %d", false)
            )
        }
    }

    override fun getCache(): HashMap<String, HashMap<String, String>> {
        return cache
    }

    override fun addToCache(lang: String, translations: HashMap<String, String>) {
        cache[lang] = translations
    }

    /**
     * Translates a map of texts to a set of languages.
     * @param texts The map of texts to translate.
     * @param source The source language.
     * @param target The set of target languages.
     */
    override fun translate(
        texts: Map<String, String>, source: String, target: Set<String>
    ) {
        val langCount: Int = target.size

        val groupedTranslations: MutableMap<Int, MutableList<String>> = LinkedHashMap()
        val requested: MutableSet<String> = HashSet()
        var groupKey = 0
        var runningTotal = 0

        for (translation in texts.values) {
            val size = translation.length * langCount
            if (runningTotal + size > MAX_CHARACTERS) {
                runningTotal = size
                groupKey++
            } else {
                runningTotal += size
            }
            requested.add(translation)
            groupedTranslations.computeIfAbsent(
                groupKey
            ) { ArrayList() }.add(translation)
        }

        if (!texts.values.containsAll(requested)) {
            val difference: MutableSet<String> = HashSet<String>(texts.values)
            difference.removeAll(requested)
            groupedTranslations[++groupKey] = ArrayList(difference)
        }

        log.info(
            "Translating {} texts in {} groups", texts.size, groupedTranslations.size
        )

        // Chain translation requests sequentially
        for (set in groupedTranslations.values) {
            val params = TranslateParams(set, target).setSourceLanguage(source)
            future(CompletableFuture.completedFuture(Unit), params).join()
        }
    }

    override fun dispose() {
        azureApi.disconnect()
    }

    private fun future(
        futureChain: CompletableFuture<Unit>, params: TranslateParams
    ): CompletableFuture<Unit> {
        var chain = futureChain
        chain = chain.thenCompose { _ ->
            azureApi.translate(params)
        }.thenApply { response ->
            onTranslate(response)
        }
        return chain
    }

    private fun onTranslate(response: Optional<TranslationResponse>) {
        if (!response.isPresent) return

        response.get().resultList.forEach(::handleResult)
    }

    private fun handleResult(result: TranslationResult) {
        result.translations.forEach { cache.computeIfAbsent(it.languageCode) { HashMap() }[result.baseText] = it.text }
    }
}