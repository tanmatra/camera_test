package camera

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter

internal class JsonConfiguration(fileName: String) : Configuration
{
    private val file = File(fileName)

    private val gson = GsonBuilder().setPrettyPrinting().create()

    private var root = JsonObject()

    init {
        load()
    }

    private fun load() {
        if (!file.exists()) {
            root = JsonObject()
            return
        }
        root = try {
            FileInputStream(file).use { inputStream ->
                InputStreamReader(inputStream).use { reader ->
                    JsonParser().parse(reader).asJsonObject
                }
            }
        } catch (e: Exception) {
            println("Error loading configuration: " + e.toString())
            JsonObject()
        }
    }

    private fun save() {
        try {
            FileOutputStream(file).use { outputStream ->
                OutputStreamWriter(outputStream).use { writer ->
                    gson.toJson(root, writer)
                }
            }
        } catch (e: IOException) {
            println("Error saving configuration: " + e.toString())
        }
    }

    private fun getCameraObject(number: Int): JsonObject {
        return root.getOrCreateObject("camera$number")
    }

    override fun getCameraURI(number: Int): String? {
        return getCameraObject(number).getAsJsonPrimitive("uri")?.asString
    }

    override fun setCameraURI(number: Int, uri: String?) {
        getCameraObject(number).addProperty("uri", uri)
        save()
    }
}

fun JsonObject.getOrCreateObject(key: String): JsonObject {
    return getAsJsonObject(key) ?: run {
        val newObject = JsonObject()
        add(key, newObject)
        newObject
    }
}
