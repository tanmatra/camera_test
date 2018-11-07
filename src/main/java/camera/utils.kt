package camera

import javafx.scene.Node
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
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

var Node.hboxGrow: Priority?
    get() = HBox.getHgrow(this)
    set(value) { HBox.setHgrow(this, value) }

var Node.vboxGrow: Priority?
    get() = VBox.getVgrow(this)
    set(value) { VBox.setVgrow(this, value) }

var Node.gridHgrow: Priority?
    get() = GridPane.getHgrow(this)
    set(value) { GridPane.setHgrow(this, value) }

var Node.gridVgrow: Priority?
    get() = GridPane.getVgrow(this)
    set(value) { GridPane.setVgrow(this, value) }

inline fun ignoreThrows(block: () -> Unit) {
    try {
        block()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
