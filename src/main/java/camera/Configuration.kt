package camera

internal interface Configuration
{
    fun getCameraURI(number: Int): String?

    fun setCameraURI(number: Int, uri: String?)
}
