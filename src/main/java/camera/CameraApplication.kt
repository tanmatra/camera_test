package camera

import javafx.application.Application
import javafx.application.Platform
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import javafx.scene.layout.BorderPane
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import javafx.stage.Stage
import uk.co.caprica.vlcj.discovery.NativeDiscovery

class CameraApplication : Application()
{
    private val configuration = JsonConfiguration("camera.json")

    private var useVlcPlayer = false

    override fun init() {
        super.init()
        val rawParameters = parameters.raw
        val forceVLC = rawParameters.contains("-vlc")
        val forceJavaFx = rawParameters.contains("-javafx")
        useVlcPlayer = forceVLC || (vlcPlayerDetected && !forceJavaFx)
    }

    override fun start(stage: Stage) {
        val gridPane = GridPane().apply {
            padding = Insets(5.0)
            hgap = 5.0
            vgap = 5.0
            add(createCameraView(1), 0, 0)
            add(createCameraView(2), 1, 0)
            add(createCameraView(3), 0, 1)
            add(createCameraView(4), 1, 1)
        }

        val menuBar = createMenuBar()

        val root = BorderPane(gridPane, menuBar, null, null, null)

        val scene = Scene(root)
        scene.stylesheets += javaClass.getResource("application.css").toExternalForm()

        stage.title = "Camera test"
        stage.scene = scene
        stage.setOnCloseRequest { quit() }
        stage.show()
    }

    private fun createCameraView(num: Int): CameraView {
        val player = if (useVlcPlayer) VlcPlayer() else JavaFxPlayer()
        val cameraView = CameraView(player, num, configuration)
        cameraView.setPrefSize(500.0, 400.0)
        GridPane.setHgrow(cameraView, Priority.ALWAYS)
        GridPane.setVgrow(cameraView, Priority.ALWAYS)
        return cameraView
    }

    private fun createMenuBar(): MenuBar {
        val exitMenuItem = MenuItem("E_xit").apply {
            setOnAction { quit() }
        }

        val fileMenu = Menu("_File").apply {
            items.addAll(exitMenuItem)
        }

        val menuBar = MenuBar(fileMenu).apply {
            isUseSystemMenuBar = true
        }

        return menuBar
    }

    private fun quit() {
        Platform.exit()
        System.exit(0)
    }

    companion object
    {
        private val vlcPlayerDetected = NativeDiscovery().discover()

        @JvmStatic
        fun main(args: Array<String>) {
            if (vlcPlayerDetected) {
                println("VLC Player detected")
            }
            Application.launch(CameraApplication::class.java, *args)
        }
    }
}
