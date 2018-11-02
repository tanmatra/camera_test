package camera;

import java.io.File;
import java.util.Optional;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;

class CameraView extends TitledPane implements Camera
{
    private static final FileChooser.ExtensionFilter ALL_FILES = new FileChooser.ExtensionFilter("All files", "*.*");

    private static final FileChooser.ExtensionFilter MP4_FILES = new FileChooser.ExtensionFilter("MP4", "*.mp4");

    private static final FileChooser.ExtensionFilter PNG_FILES = new FileChooser.ExtensionFilter("PNG", "*.png");

    private static final FileChooser.ExtensionFilter JPG_FILES = new FileChooser.ExtensionFilter("JPG", "*.jpg");

    private final Configuration configuration;

    private int cameraNum;

    private String uri;

    private final Player player;

    CameraView(Player player, int number, Configuration configuration) {
        this.player = player;
        this.configuration = configuration;
        setCollapsible(false);
        setCamNum(number);

        final BorderPane content = new BorderPane();
        content.getStyleClass().add("video-pane");
        setContent(content);

        final Pane mediaViewPane = new BorderPane(player.getViewNode());
        mediaViewPane.getStyleClass().add("media-view-pane");
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

    private void openFile() {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open video file");
        fileChooser.getExtensionFilters().addAll(ALL_FILES, MP4_FILES);
        fileChooser.setSelectedExtensionFilter(MP4_FILES);
        final File oldFile = UriUtil.uriToFile(uri);
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
        player.stop();
    }

    private void play() {
        player.play();
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
        player.setSource(uri);
        this.uri = uri;
        configuration.setCameraURI(cameraNum, uri);
    }

    @Override
    public String getEncoder() {
        return uri;
    }

    @Override
    public int getFps() {
        return player.getFps();
    }

    @Override
    public Image getScreenshot() {
        return player.getScreenshot();
    }

    @Override
    public void makeScreenshot(String path) {
        player.makeScreenshot(path);
    }
}
