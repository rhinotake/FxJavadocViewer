package jp.rhino.take

import com.sun.deploy.util.FXLoader
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.layout.AnchorPane
import javafx.stage.Stage

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        Application.launch(App::class.java, *args)
    }

    class App : Application() {
        override fun start(stage: Stage) {
            val loader = FXMLLoader(this.javaClass.getResource("./MainPanel.fxml"))
            val anchorPane:AnchorPane = loader.load()
            val mainPanel:MainPanel = loader.getController()

            stage.scene = Scene(anchorPane)
            stage.title = Main::class.qualifiedName
            stage.show()
        }
    }
}