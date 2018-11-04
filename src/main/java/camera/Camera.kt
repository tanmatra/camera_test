package camera

import javafx.scene.image.Image

interface Camera
{
    /** Camera number */
    var camNum: Int

    /** Encoder stream URI */
    var encoder: String?

    /** Get current FPS */
    val fps: Int

    /** Get the screenshot */
    val screenshot: Image

    /** Make the screenshot and save it to the specified path */
    fun makeScreenshot(path: String)

    companion object
    {
        /** Minimum camera number  */
        const val CAM_NUM_MIN = 1

        /** Maximum camera number  */
        const val CAM_NUM_MAX = 9999
    }
}
