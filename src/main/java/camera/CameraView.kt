package camera

import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.control.Separator
import javafx.scene.control.TextInputDialog
import javafx.scene.control.TitledPane
import javafx.scene.image.Image
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.stage.FileChooser

internal class CameraView(
    private val player: Player,
    number: Int,
    private val configuration: Configuration
) : TitledPane(), Camera
{
    override var camNum: Int = 0
        set(cameraNum) {
            field = cameraNum
            text = "Camera $cameraNum"
        }

    override var encoder: String? = null
        set(uri) {
            player.setSource(uri)
            field = uri
            configuration.setCameraURI(camNum, uri)
        }

    override val fps: Int get() = player.fps

    override val screenshot: Image get() = player.screenshot

    init {
        isCollapsible = false
        camNum = number

        val content = BorderPane()
        content.styleClass.add("video-pane")
        setContent(content)

        val mediaViewPane = BorderPane(player.viewNode)
        mediaViewPane.styleClass += "media-view-pane"
        content.center = mediaViewPane

        content.bottom = createButtonsPane()

        configuration.getCameraURI(number)?.let { oldURI ->
            ignoreThrows {
                encoder = oldURI
            }
        }
    }

    private fun createButtonsPane(): Node {
        val openFileButton = Button("Open file")
        openFileButton.setOnAction { openFile() }

        val openURIButton = Button("Open URI")
        openURIButton.setOnAction { openURI() }

        val playButton = Button("Play")
        playButton.setOnAction { play() }

        val stopButton = Button("Stop")
        stopButton.setOnAction { stop() }

        val screenshotButton = Button("Screenshot")
        screenshotButton.setOnAction { saveScreenshotToFile() }

        return HBox(
            openURIButton,
            openFileButton,
            Separator(Orientation.VERTICAL),
            playButton,
            stopButton,
            Separator(Orientation.VERTICAL),
            screenshotButton
        ).apply {
            alignment = Pos.CENTER_LEFT
            padding = Insets(5.0)
            spacing = 5.0
        }
    }

    private fun saveScreenshotToFile() {
        val fileChooser = FileChooser()
        fileChooser.extensionFilters.addAll(PNG_FILES, JPG_FILES, ALL_FILES)
        val file = fileChooser.showSaveDialog(scene.window) ?: return
        tryWithAlert("Error saving screenshot") {
            makeScreenshot(file.toString())
        }
    }

    private fun openFile() {
        val fileChooser = FileChooser().apply {
            title = "Open video file"
            extensionFilters.addAll(ALL_FILES, MP4_FILES)
            selectedExtensionFilter = MP4_FILES
            uriToFile(encoder)?.let { oldFile ->
                initialDirectory = oldFile.parentFile
                initialFileName = oldFile.name
            }
        }
        val file = fileChooser.showOpenDialog(scene.window)
        if (file != null) {
            tryWithAlert("File open error") {
                encoder = file.toURI().toString()
            }
        }
    }

    private fun openURI() {
        val dialog = TextInputDialog(encoder).apply {
            title = "Open URI"
            headerText = "Enter streaming URI"
            contentText = "URI"
            dialogPane.prefWidth = 600.0
            isResizable = true
        }
        val result = dialog.showAndWait()
        tryWithAlert("URI open error") {
            result.ifPresent { encoder = it }
        }
    }

    private fun stop() {
        player.stop()
    }

    private fun play() {
        player.play()
    }

    override fun makeScreenshot(path: String) {
        player.makeScreenshot(path)
    }

    companion object
    {
        private val ALL_FILES = FileChooser.ExtensionFilter("All files", "*.*")

        private val MP4_FILES = FileChooser.ExtensionFilter("MP4", "*.mp4")

        private val PNG_FILES = FileChooser.ExtensionFilter("PNG", "*.png")

        private val JPG_FILES = FileChooser.ExtensionFilter("JPG", "*.jpg")

        private fun showAlert(headerText: String, e: Exception) {
            Alert(Alert.AlertType.ERROR, e.toString()).run {
                title = "Error"
                this.headerText = headerText
                showAndWait()
            }
        }

        inline private fun tryWithAlert(headerText: String, code: () -> Unit) {
            try {
                code()
            } catch (e: Exception) {
                showAlert(headerText, e)
            }
        }
    }
}
