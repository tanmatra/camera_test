package camera;

import com.sun.jna.Memory;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritablePixelFormat;
import uk.co.caprica.vlcj.component.DirectMediaPlayerComponent;
import uk.co.caprica.vlcj.player.direct.BufferFormat;
import uk.co.caprica.vlcj.player.direct.BufferFormatCallback;
import uk.co.caprica.vlcj.player.direct.DefaultDirectMediaPlayer;
import uk.co.caprica.vlcj.player.direct.DirectMediaPlayer;
import uk.co.caprica.vlcj.player.direct.format.RV32BufferFormat;

class VlcPlayer extends Player
{
    private final Canvas canvas = new Canvas();

    private final PixelWriter pixelWriter = canvas.getGraphicsContext2D().getPixelWriter();

    private final WritablePixelFormat<ByteBuffer> pixelFormat = PixelFormat.getByteBgraPreInstance();

    private final BufferFormatCallback bufferFormatCallback = (int sourceWidth, int sourceHeight) -> {
        final int width = 400;
        final int height = width * sourceHeight / sourceWidth;
        Platform.runLater(() -> {
            canvas.setWidth(width);
            canvas.setHeight(height);
        });
        return new RV32BufferFormat(width, height);
    };

    private final DirectMediaPlayerComponent playerComponent = new DirectMediaPlayerComponent(bufferFormatCallback);

    private final DirectMediaPlayer mediaPlayer = playerComponent.getMediaPlayer();

    private final AnimationTimer animationTimer = new AnimationTimer() {
        @Override
        public void handle(long now) {
            renderFrame();
        }
    };

    VlcPlayer() {
        canvas.setWidth(400.0);
        canvas.setHeight(300.0);
        mediaPlayer.setRepeat(true);
        mediaPlayer.setVolume(5);
    }

    @Override
    Node getViewNode() {
        return canvas;
    }

    @Override
    void play() {
        mediaPlayer.play();
        animationTimer.start();
    }

    @Override
    void stop() {
        animationTimer.stop();
        mediaPlayer.stop();
        // mediaPlayer.release();
    }

    @Override
    void setSource(String uri) {
        final File file = UriUtil.uriToFile(uri);
        final String media = (file != null) ? file.toString() : uri;
        mediaPlayer.playMedia(media);
        play();
    }

    @Override
    int getFps() {
        return Math.round(mediaPlayer.getFps());
    }

    @Override
    void makeScreenshot(String path) {
        mediaPlayer.saveSnapshot(new File(path));
    }

    @Override
    Image getScreenshot() {
        final BufferedImage bufferedImage = mediaPlayer.getSnapshot();
        return SwingFXUtils.toFXImage(bufferedImage, null);
    }

    private void renderFrame() {
        final Memory[] nativeBuffers = mediaPlayer.lock();
        try {
            if (nativeBuffers != null) {
                final Memory nativeBuffer = nativeBuffers[0];
                if (nativeBuffer != null) {
                    final ByteBuffer byteBuffer = nativeBuffer.getByteBuffer(0, nativeBuffer.size());
                    final BufferFormat bufferFormat = ((DefaultDirectMediaPlayer) mediaPlayer).getBufferFormat();
                    final int width = bufferFormat.getWidth();
                    final int height = bufferFormat.getHeight();
                    if (width > 0 && height > 0) {
                        pixelWriter.setPixels(0, 0, width, height, pixelFormat, byteBuffer,
                                bufferFormat.getPitches()[0]);
                    }
                }
            }
        } finally {
            mediaPlayer.unlock();
        }
    }
}
