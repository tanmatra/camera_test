package camera

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.Properties

internal class PropertiesConfiguration(fileName: String) : Configuration
{
    private val properties = Properties()

    private val file: File

    init {
        file = File(fileName)
        load()
    }

    private fun load() {
        if (!file.exists()) {
            return
        }
        try {
            FileInputStream(file).use { inputStream -> properties.load(inputStream) }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun save() {
        try {
            FileOutputStream(file).use { outputStream -> properties.store(outputStream, null) }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun uriKey(number: Int) = "camera.$number.uri"

    override fun getCameraURI(number: Int): String {
        return properties.getProperty(uriKey(number))
    }

    override fun setCameraURI(number: Int, uri: String?) {
        properties.setProperty(uriKey(number), uri)
        save()
    }
}
