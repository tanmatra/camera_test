package camera;

import javafx.scene.image.Image;

public interface Camera
{
    /** Minimum camera number */
    int CAM_NUM_MIN = 1;

    /** Maximum camera number */
    int CAM_NUM_MAX = 9999;

    /** Set the camera number */
    void setCamNum(int cn);

    /** Get the camera number */
    int getCamNum();

    /** Set the encoder stream URI */
    void setEncoder(String enc);

    /** Get the encoder stream URI */
    String getEncoder();

    /** Get current FPS */
    int getFps();

    /** Get the screenshot */
    Image getScreenshot();

    /** Make the screenshot and save it to the specified path */
    void makeScreenshot(String path);
}
