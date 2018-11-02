package camera;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;

public class CameraApplication extends Application
{
    private final Configuration configuration = new JsonConfiguration("camera.json");

    private static final boolean vlcPlayerDetected = new NativeDiscovery().discover();

    public static void main(String[] args) {
        if (vlcPlayerDetected) {
            System.out.println("VLC Player detected");
        }
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        final GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(5.0));
        gridPane.setHgap(5.0);
        gridPane.setVgap(5.0);
        gridPane.add(createCameraView(1), 0, 0);
        gridPane.add(createCameraView(2), 1, 0);
        gridPane.add(createCameraView(3), 0, 1);
        gridPane.add(createCameraView(4), 1, 1);

        final MenuBar menuBar = createMenuBar();

        final BorderPane root = new BorderPane(gridPane, menuBar, null, null, null);

        final Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

        stage.setTitle("Camera test");
        stage.setScene(scene);
        stage.setOnCloseRequest(windowEvent -> quit());
        stage.show();
    }

    private CameraView createCameraView(int num) {
        final Player player = vlcPlayerDetected ? new VlcPlayer() : new JavaFxPlayer();
        final CameraView cameraView = new CameraView(player, num, configuration);
        cameraView.setPrefSize(500, 400);
        GridPane.setHgrow(cameraView, Priority.ALWAYS);
        GridPane.setVgrow(cameraView, Priority.ALWAYS);
        return cameraView;
    }

    private MenuBar createMenuBar() {
        final MenuItem exitMenuItem = new MenuItem("E_xit");
        exitMenuItem.setOnAction(event -> quit());

        final Menu fileMenu = new Menu("_File");
        fileMenu.getItems().addAll(exitMenuItem);

        final MenuBar menuBar = new MenuBar(fileMenu);
        menuBar.setUseSystemMenuBar(true);

        return menuBar;
    }

    private void quit() {
        Platform.exit();
        System.exit(0);
    }
}
