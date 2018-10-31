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
import javafx.stage.Stage;

public class CameraApplication extends Application
{
    private final Configuration configuration = new PropertiesConfiguration("camera.properties");

    public static void main(String[] args) {
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
        stage.show();
    }

    private CameraView createCameraView(int num) {
        final CameraView cameraView = new CameraView(num, configuration);
        cameraView.setPrefSize(500, 400);
        return cameraView;
    }

    private MenuBar createMenuBar() {
        final MenuItem exitMenuItem = new MenuItem("E_xit");
        exitMenuItem.setOnAction(event -> Platform.exit());

        final Menu fileMenu = new Menu("_File");
        fileMenu.getItems().addAll(exitMenuItem);

        final MenuBar menuBar = new MenuBar(fileMenu);
        menuBar.setUseSystemMenuBar(true);

        return menuBar;
    }
}
