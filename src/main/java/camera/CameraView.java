package camera;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;

class CameraView extends TitledPane implements Camera
{
    private static final FileChooser.ExtensionFilter ALL_FILES = new FileChooser.ExtensionFilter("All files", "*.*");

    private static final FileChooser.ExtensionFilter MP4_FILES = new FileChooser.ExtensionFilter("MP4", "*.mp4");

    private static final FileChooser.ExtensionFilter PNG_FILES = new FileChooser.ExtensionFilter("PNG", "*.png");

    private static final FileChooser.ExtensionFilter JPG_FILES = new FileChooser.ExtensionFilter("JPG", "*.jpg");

    private final Configuration configuration;

    private int cameraNum;

    private final MediaView mediaView;

    private MediaPlayer mediaPlayer;

    private String uri;

    CameraView(int number, Configuration configuration) {
        this.configuration = configuration;
        setCollapsible(false);
        setCamNum(number);

        mediaView = new MediaView(mediaPlayer);
        mediaView.setFitWidth(400.0);

        final BorderPane content = new BorderPane();
        content.getStyleClass().add("video-pane");
        setContent(content);

        final Pane mediaViewPane = new BorderPane(mediaView);
        mediaViewPane.getStyleClass().add("media-view-pane");
        // mediaView.fitWidthProperty().bind(mediaViewPane.widthProperty());
        content.setCenter(mediaViewPane);

        content.setBottom(createButtonsPane());

        final String oldURI = configuration.getCameraURI(number);
        if (oldURI != null) {
            try {
                setEncoder(oldURI);
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }
    }

    private Node createButtonsPane() {
        final Button openFileButton = new Button("Open file");
        openFileButton.setOnAction(event -> openFile());

        final Button openURIButton = new Button("Open URI");
        openURIButton.setOnAction(event -> openURI());

        final Button playButton = new Button("Play");
        playButton.setOnAction(event -> play());

        final Button stopButton = new Button("Stop");
        stopButton.setOnAction(event -> stop());

        final Button screenshotButton = new Button("Screenshot");
        screenshotButton.setOnAction(event -> saveScreenshotToFile());

        final HBox buttonsPanel = new HBox(
                openURIButton,
                openFileButton,
                new Separator(Orientation.VERTICAL),
                playButton,
                stopButton,
                new Separator(Orientation.VERTICAL),
                screenshotButton);
        buttonsPanel.setAlignment(Pos.CENTER_LEFT);
        buttonsPanel.setPadding(new Insets(5.0));
        buttonsPanel.setSpacing(5.0);

        return buttonsPanel;
    }

    private void saveScreenshotToFile() {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(PNG_FILES, JPG_FILES, ALL_FILES);
        final File file = fileChooser.showSaveDialog(getScene().getWindow());
        if (file == null) {
            return;
        }
        try {
            makeScreenshot(file.toString());
        } catch (Exception e) {
            final Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Error saving screenshot");
            alert.setContentText(e.toString());
            alert.showAndWait();
        }
    }

    private static File uriToFile(String uriString) {
        if (uriString == null) {
            return null;
        }
        final URI fileURI;
        try {
            fileURI = new URI(uriString);
        } catch (URISyntaxException e) {
            return null;
        }
        final File file;
        try {
            file = new File(fileURI);
        } catch (IllegalArgumentException e) {
            return null;
        }
        return file;
    }

    private void openFile() {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open video file");
        fileChooser.getExtensionFilters().addAll(ALL_FILES, MP4_FILES);
        fileChooser.setSelectedExtensionFilter(MP4_FILES);
        final File oldFile = uriToFile(uri);
        if (oldFile != null) {
            fileChooser.setInitialDirectory(oldFile.getParentFile());
            fileChooser.setInitialFileName(oldFile.getName());
        }
        final File file = fileChooser.showOpenDialog(getScene().getWindow());
        if (file != null) {
            try {
                setEncoder(file.toURI().toString());
            } catch (Exception e) {
                showAlert("File open error", e);
            }
        }
    }

    private void openURI() {
        final TextInputDialog dialog = new TextInputDialog(uri);
        dialog.setTitle("Open URI");
        dialog.setHeaderText("Enter streaming URI");
        dialog.setContentText("URI");
        dialog.getDialogPane().setPrefWidth(600.0);
        dialog.setResizable(true);
        final Optional<String> result = dialog.showAndWait();
        try {
            result.ifPresent(this::setEncoder);
        } catch (Exception e) {
            showAlert("URI open error", e);
        }
    }

    private static void showAlert(String headerText, Exception e) {
        final Alert alert = new Alert(Alert.AlertType.ERROR, e.toString());
        alert.setTitle("Error");
        alert.setHeaderText(headerText);
        alert.showAndWait();
    }

    private void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    private void play() {
        if (mediaPlayer != null) {
            mediaPlayer.play();
        }
    }

    @Override
    public void setCamNum(int cameraNum) {
        this.cameraNum = cameraNum;
        setText("Camera " + cameraNum);
    }

    @Override
    public int getCamNum() {
        return cameraNum;
    }

    @Override
    public void setEncoder(String uri) {
        stop();
        final Media media = new Media(uri);
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setAutoPlay(true);
        mediaPlayer.setCycleCount(Integer.MAX_VALUE);
        mediaPlayer.setVolume(0.025);
        mediaView.setMediaPlayer(mediaPlayer);
        this.uri = uri;
        configuration.setCameraURI(cameraNum, uri);
    }

    @Override
    public String getEncoder() {
        return uri;
    }

    @Override
    public int getFps() {
        return 0; //TODO
    }

    @Override
    public Image getScreenshot() {
        final Bounds bounds = mediaView.getBoundsInLocal();
        final WritableImage image = new WritableImage((int) bounds.getWidth(), (int) bounds.getHeight());
        return mediaView.snapshot(null, image);
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
