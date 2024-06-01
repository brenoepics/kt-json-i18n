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

    override fun getLanguageCache(langCode: String): HashMap<String, String> {
        return cache[langCode] ?: HashMap()
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
        val (langCount: Int, requests: MutableMap<Int, MutableList<String>>) = parseRequests(target, texts)
        log.info(
            "Translating {} texts to {} languages with {} requests", texts.size, langCount, requests.size
        )

        // Chain translation requests sequentially
        for (set in requests.values) {
            val params = TranslateParams(set, target).setSourceLanguage(source)
            future(CompletableFuture.completedFuture(Unit), params).join()
        }
    }

    /**
     * Parses the translation requests based on the maximum characters allowed.
     * @param target The set of target languages.
     * @param texts The map of texts to translate.
     * @return A pair with the number of target languages and the translation requests.
     * @link https://docs.microsoft.com/en-us/azure/cognitive-services/translator/request-limits
     * @see MAX_CHARACTERS
     * @see TranslateParams
     */
    private fun parseRequests(
        target: Set<String>,
        texts: Map<String, String>
    ): Pair<Int, MutableMap<Int, MutableList<String>>> {
        val langCount: Int = target.size

        val requests: MutableMap<Int, MutableList<String>> = LinkedHashMap()
        val requested: MutableSet<String> = HashSet()
        var reqKey = 0
        var runningTotal = 0

        for (translation in texts.values) {
            val size = translation.length * langCount
            if (runningTotal + size > MAX_CHARACTERS) {
                runningTotal = size
                reqKey++
            } else {
                runningTotal += size
            }
            requested.add(translation)
            requests.computeIfAbsent(
                reqKey
            ) { ArrayList() }.add(translation)
        }

        if (!texts.values.containsAll(requested)) {
            val difference: MutableSet<String> = HashSet<String>(texts.values)
            difference.removeAll(requested)
            requests[++reqKey] = ArrayList(difference)
        }
        return Pair(langCount, requests)
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
        if (response.isPresent) {
            response.get().resultList.forEach(::handleResult)
        }
    }

    private fun handleResult(result: TranslationResult) {
        result.translations.forEach { cache.computeIfAbsent(it.languageCode) { HashMap() }[result.baseText] = it.text }
    }

    override fun dispose() {
        azureApi.disconnect()
    }
}