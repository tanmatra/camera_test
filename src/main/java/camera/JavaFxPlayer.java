package camera;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javax.imageio.ImageIO;

class JavaFxPlayer extends Player
{
    private final MediaView mediaView = new MediaView();

    private MediaPlayer mediaPlayer;

    JavaFxPlayer() {
        mediaView.setFitWidth(400.0);
        // mediaView.fitWidthProperty().bind(mediaViewPane.widthProperty());
    }

    @Override
    Node getViewNode() {
        return mediaView;
    }

    @Override
    void setSource(String uri) {
        stop();
        final Media media = new Media(uri);
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setAutoPlay(true);
        mediaPlayer.setCycleCount(Integer.MAX_VALUE);
        mediaPlayer.setVolume(0.025);
        mediaView.setMediaPlayer(mediaPlayer);
    }

    @Override
    void play() {
        if (mediaPlayer != null) {
            mediaPlayer.play();
        }
    }

    @Override
    void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    @Override
    float getFps() {
        return 0.0f;
    }

    @Override
    Image getScreenshot() {
        return mediaView.snapshot(null, null);
    }

    @Override
    public void makeScreenshot(String path) {
        final int dotIndex = path.lastIndexOf('.');
        if (dotIndex < 0) {
            throw new RuntimeException("Cannot determine image file format");
        }
        final String format = path.substring(dotIndex + 1);
        final Image screenshot = getScreenshot();
        final BufferedImage bufferedImage = SwingFXUtils.fromFXImage(screenshot, null);
        final boolean saved;
        try {
            saved = ImageIO.write(bufferedImage, format, new File(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (!saved) {
            throw new RuntimeException("Unsupported image writer");
        }
    }
}
