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

    private var root: JsonObject? = null

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
                    val parser = JsonParser()
                    parser.parse(reader).asJsonObject
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
        val key = "camera$number"
        var `object`: JsonObject? = root!!.getAsJsonObject(key)
        if (`object` == null) {
            `object` = JsonObject()
            root!!.add(key, `object`)
        }
        return `object`
    }

    override fun getCameraURI(number: Int): String? {
        val cameraObject = getCameraObject(number)
        val primitive = cameraObject.getAsJsonPrimitive("uri")
        return primitive?.asString
    }

    override fun setCameraURI(number: Int, uri: String?) {
        val cameraObject = getCameraObject(number)
        cameraObject.addProperty("uri", uri)
        save()
    }
}
