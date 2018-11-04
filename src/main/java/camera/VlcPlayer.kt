package camera

import javafx.animation.AnimationTimer
import javafx.application.Platform
import javafx.embed.swing.SwingFXUtils
import javafx.scene.Node
import javafx.scene.canvas.Canvas
import javafx.scene.image.Image
import javafx.scene.image.PixelFormat
import uk.co.caprica.vlcj.component.DirectMediaPlayerComponent
import uk.co.caprica.vlcj.player.direct.DefaultDirectMediaPlayer
import uk.co.caprica.vlcj.player.direct.format.RV32BufferFormat
import java.io.File

internal class VlcPlayer : Player()
{
    private val canvas = Canvas()

    private val pixelWriter = canvas.graphicsContext2D.pixelWriter

    private val pixelFormat = PixelFormat.getByteBgraPreInstance()

    private val bufferFormatCallback = { sourceWidth: Int, sourceHeight: Int ->
        val width = 400
        val height = width * sourceHeight / sourceWidth
        Platform.runLater {
            canvas.width = width.toDouble()
            canvas.height = height.toDouble()
        }
        RV32BufferFormat(width, height)
    }

    private val playerComponent = DirectMediaPlayerComponent(bufferFormatCallback)

    private val mediaPlayer = playerComponent.mediaPlayer

    private val animationTimer = object : AnimationTimer() {
        override fun handle(now: Long) {
            renderFrame()
        }
    }

    override val viewNode: Node
        get() = canvas

    override val fps: Int
        get() = Math.round(mediaPlayer.fps)

    override val screenshot: Image
        get() {
            val bufferedImage = mediaPlayer.snapshot
            return SwingFXUtils.toFXImage(bufferedImage, null)
        }

    init {
        canvas.width = 400.0
        canvas.height = 300.0
        mediaPlayer.repeat = true
        mediaPlayer.volume = 5
    }

    override fun play() {
        mediaPlayer.play()
        animationTimer.start()
    }

    override fun stop() {
        animationTimer.stop()
        mediaPlayer.stop()
        // mediaPlayer.release();
    }

    override fun setSource(uri: String?) {
        val file = uriToFile(uri)
        val media = file?.toString() ?: uri
        mediaPlayer.playMedia(media)
        play()
    }

    override fun makeScreenshot(path: String) {
        mediaPlayer.saveSnapshot(File(path))
    }

    private fun renderFrame() {
        val nativeBuffers = mediaPlayer.lock()
        try {
            if (nativeBuffers != null) {
                val nativeBuffer = nativeBuffers[0]
                if (nativeBuffer != null) {
                    val byteBuffer = nativeBuffer.getByteBuffer(0, nativeBuffer.size())
                    val bufferFormat = (mediaPlayer as DefaultDirectMediaPlayer).bufferFormat
                    val width = bufferFormat.width
                    val height = bufferFormat.height
                    if (width > 0 && height > 0) {
                        pixelWriter.setPixels(
                            0, 0, width, height, pixelFormat, byteBuffer,
                            bufferFormat.pitches[0])
                    }
                }
            }
        } finally {
            mediaPlayer.unlock()
        }
    }
}
