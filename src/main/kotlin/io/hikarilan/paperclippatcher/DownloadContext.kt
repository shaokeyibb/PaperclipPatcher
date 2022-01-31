package io.hikarilan.paperclippatcher

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.util.*

sealed interface DownloadContext<S> {

    var sourceUrl: String
    val version: String

    fun apply(): S

}

class PropertiesDownloadContext private constructor(
    override var sourceUrl: String,
    override val version: String,
    private val patch: String,
    private val originalHash: String,
    private val patchedHash: String,
) : DownloadContext<Properties> {

    constructor(data: ByteArray) : this(Properties().apply { load(data.inputStream()) })

    private constructor(data: Properties) : this(
        data.getProperty("sourceUrl"),
        data.getProperty("version"),
        data.getProperty("patch"),
        data.getProperty("originalHash"),
        data.getProperty("patchedHash")
    )

    override fun apply(): Properties {
        return Properties().apply {
            this.setProperty("sourceUrl", sourceUrl)
            this.setProperty("version", version)
            this.setProperty("patch", patch)
            this.setProperty("originalHash", originalHash)
            this.setProperty("patchedHash", patchedHash)
        }
    }

}

class JsonDownloadContext private constructor(
    override var sourceUrl: String,
    override val version: String,
    private val patch: String,
    private val originalHash: String,
    private val patchedHash: String,
) : DownloadContext<JsonObject> {

    constructor(data: ByteArray) : this(JsonParser.parseReader(data.inputStream().reader()).asJsonObject)

    private constructor(data: JsonObject) : this(
        data.get("sourceUrl").asString,
        data.get("version").asString,
        data.get("patch").asString,
        data.get("originalHash").asString,
        data.get("patchedHash").asString
    )

    override fun apply(): JsonObject {
        return JsonObject().apply {
            addProperty("sourceUrl", sourceUrl)
            addProperty("version", version)
            addProperty("patch", patch)
            addProperty("originalHash", originalHash)
            addProperty("patchedHash", patchedHash)
        }
    }

}

class PlainTextDownloadContext private constructor(
    override var sourceUrl: String,
    override val version: String,
    private val originalHash: String,
    private val fileName: String,
) : DownloadContext<String> {

    constructor(data: ByteArray, version: ByteArray) : this(
        data.inputStream().reader().readText(),
        JsonParser.parseReader(version.inputStream().reader()).asJsonObject
    )

    private constructor(data: String, version: JsonObject) : this(
        data.split("\t"),
        version.getAsJsonPrimitive("id").asString
    )

    private constructor(data: List<String>, version: String) : this(data[1], version, data[0], data[2])

    override fun apply(): String {
        return "$originalHash\t$sourceUrl\t$fileName"
    }
}