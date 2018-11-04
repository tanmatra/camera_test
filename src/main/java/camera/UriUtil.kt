package camera

import java.io.File
import java.net.URI
import java.net.URISyntaxException

fun uriToFile(uriString: String?): File? {
    if (uriString == null) {
        return null
    }
    val fileURI = try {
        URI(uriString)
    } catch (e: URISyntaxException) {
        return null
    }
    return try {
        File(fileURI)
    } catch (e: IllegalArgumentException) {
        return null
    }
}

