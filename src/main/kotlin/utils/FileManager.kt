package io.github.brenoepics.utils

import org.ini4j.Ini
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import org.slf4j.LoggerFactory
import translation.TranslationException
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

class FileManager {
    companion object {
        private val log = LoggerFactory.getLogger(FileManager::class.java)
        fun readPath(path: Path, recursive: Boolean): List<Path> {
            if (!Files.exists(path)) throw TranslationException("Path does not exist: $path")

            if (!Files.isDirectory(path)) {
                return listOf(path)
            }

            if (recursive) {
                return readPathRecursive(path)
            }

            Files.newDirectoryStream(path, "*.json").use { paths ->
                return paths.toList()
            }
        }

        private fun readPathRecursive(path: Path): List<Path> {
            val result = mutableListOf<Path>()
            Files.walk(path).use { paths ->
                paths.filter { it.toString().endsWith(".json") }.forEach { result.add(it) }
            }
            return result
        }

        fun readJsonFile(path: Path): Any? {
            val jsonStr = String(Files.readAllBytes(path))
            return readJSON(jsonStr)
        }

        private fun readJSON(str: String): Any? {
            return try {
                JSONObject(JSONTokener(str))
            } catch (e: Exception) {
                try {
                    JSONArray(JSONTokener(str))
                } catch (e: Exception) {
                    log.error("Error reading JSON string: $str", e)
                    null
                }
            }
        }

        @Throws(IOException::class)
        private fun getNewFilePath(
            outputTemplate: String, sourceDirPath: Path, outputDirPath: Path?, path: Path, lang: String
        ): Path? {
            val relativePath = sourceDirPath.relativize(path)
            val fileName = relativePath.fileName.toString()
            var subDir = ""
            if (relativePath.parent != null && relativePath.parent.toString() != outputDirPath.toString()) {
                subDir = relativePath.parent.toString() + "/"
            }
            val inputFilename = fileName.substringBeforeLast(".")
            val inputExtension = fileName.substringAfterLast(".")

            val newFileName = generateTemplate(outputTemplate, subDir, lang, inputFilename, inputExtension)

            val newFilePath = outputDirPath?.resolve(newFileName)
            if (newFilePath != null) {
                Files.createDirectories(newFilePath.parent)
            }
            return newFilePath
        }

        private fun generateTemplate(
            outputTemplate: String, subDir: String, lang: String, inputFilename: String, inputExtension: String
        ): String {
            return outputTemplate.replace("{sub_dir}", subDir).replace("{to_lang}", lang)
                .replace("{input_filename}", inputFilename).replace("{input_extension}", inputExtension)
        }

        private fun writeJsonFile(newFilePath: Path, newJson: Any?, indentation: Int) {
            if (newJson as? JSONObject != null) {
                Files.write(newFilePath, newJson.toString(indentation).toByteArray())
                return
            }
            if (newJson as? JSONArray != null) {
                Files.write(newFilePath, newJson.toString(indentation).toByteArray())
                return
            }
            log.error("Error writing JSON file: $newJson, JSON is not JSONObject or JSONArray")
        }

        private fun replaceTextInJson(obj: Any, translations: Map<String, String>): Any {
            when (obj) {
                is JSONObject -> for (key in obj.keySet()) {
                    obj.put(key, replaceTextInJson(obj[key], translations))
                }

                is JSONArray -> for (i in 0 until obj.length()) {
                    obj.put(i, replaceTextInJson(obj[i], translations))
                }

                is String -> return translations[obj] ?: obj
            }
            return obj
        }

        fun getJsonTexts(obj: Any?): HashMap<String, String> {
            if (obj == null) return HashMap()
            val texts = HashMap<String, String>()
            return getJsonTexts(obj, texts)
        }

        fun getJsonTexts(obj: Any, texts: HashMap<String, String>): HashMap<String, String> {
            when (obj) {
                is JSONObject -> obj.keySet().forEach { key -> getJsonTexts(obj[key], texts) }
                is JSONArray -> (0 until obj.length()).forEach { i -> getJsonTexts(obj[i], texts) }
                is String -> texts[obj] = obj
            }
            return texts
        }

        fun Map<String, String>.writeTranslation(
            template: String,
            sourceDirPath: Path,
            json: Any?,
            outputDirPath: Path?,
            path: Path,
            lang: String,
            indentation: Int
        ) {
            if (json != null) {
                replaceTextInJson(json, this)
            }
            val newFilePath = outputDirPath?.let {
                try {
                    getNewFilePath(template, sourceDirPath, outputDirPath, path, lang)
                } catch (e: IOException) {
                    log.error("Error creating new file path (check your output_template)", e)
                    null
                }
            }
            if (newFilePath != null) {
                writeJsonFile(newFilePath, json, indentation)
            }
        }

        @Throws(IOException::class)
        fun readIni(): Ini {
            val fileToParse = File("config.ini")
            return Ini(fileToParse)
        }
    }
}