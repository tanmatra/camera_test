package camera;

import javafx.scene.Node;
import javafx.scene.image.Image;

abstract class Player
{
    abstract int getFps();

    abstract void play();

    abstract void stop();

    abstract void makeScreenshot(String path);

    abstract Image getScreenshot();

    abstract Node getViewNode();

    abstract void setSource(String uri);
}
