package camera;

interface Configuration
{
    String getCameraURI(int number);

    void setCameraURI(int number, String uri);
}
