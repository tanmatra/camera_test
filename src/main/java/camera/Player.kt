package camera

import javafx.scene.Node
import javafx.scene.image.Image

internal abstract class Player
{
    internal abstract val fps: Int

    internal abstract val screenshot: Image

    internal abstract val viewNode: Node

    internal abstract fun play()

    internal abstract fun stop()

    internal abstract fun makeScreenshot(path: String)

    internal abstract fun setSource(uri: String?)
}
