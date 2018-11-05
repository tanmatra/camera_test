package camera

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.Properties

internal class PropertiesConfiguration(fileName: String) : Configuration
{
    private val properties = Properties()

    private val file = File(fileName)

    init {
        load()
    }

    private fun load() {
        if (!file.exists()) {
            return
        }
        ignoreThrows {
            FileInputStream(file).use { inputStream -> properties.load(inputStream) }
        }
    }

    private fun save() {
        ignoreThrows {
            FileOutputStream(file).use { outputStream -> properties.store(outputStream, null) }
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
