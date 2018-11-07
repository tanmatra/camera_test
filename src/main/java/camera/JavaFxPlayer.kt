package camera

import javafx.embed.swing.SwingFXUtils
import javafx.scene.Node
import javafx.scene.image.Image
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import javafx.scene.media.MediaView
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO

internal class JavaFxPlayer : Player()
{
    private val mediaView = MediaView()

    private var mediaPlayer: MediaPlayer? = null

    override val viewNode: Node get() = mediaView

    override val fps: Float get() = 0.0f

    override val screenshot: Image
        get() = mediaView.snapshot(null, null)

    init {
        mediaView.fitWidth = 400.0
        // mediaView.fitWidthProperty().bind(mediaViewPane.widthProperty());
    }

    override fun setSource(uri: String?) {
        stop()
        val media = Media(uri)
        mediaPlayer = MediaPlayer(media).apply {
            isAutoPlay = true
            cycleCount = Integer.MAX_VALUE
            volume = 0.025
        }
        mediaView.mediaPlayer = mediaPlayer
    }

    override fun play() {
        mediaPlayer?.play()
    }

    override fun stop() {
        mediaPlayer?.stop()
    }

    override fun makeScreenshot(path: String) {
        val dotIndex = path.lastIndexOf('.')
        if (dotIndex < 0) {
            throw RuntimeException("Cannot determine image file format")
        }
        val format = path.substring(dotIndex + 1)
        val screenshot = screenshot
        val bufferedImage = SwingFXUtils.fromFXImage(screenshot, null)
        val saved: Boolean
        try {
            saved = ImageIO.write(bufferedImage, format, File(path))
        } catch (e: IOException) {
            throw RuntimeException(e)
        }

        if (!saved) {
            throw RuntimeException("Unsupported image writer")
        }
    }
}
