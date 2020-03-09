package jp.rhino.take

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.layout.AnchorPane
import javafx.stage.Stage

object Main {
  @JvmStatic
  fun main(args: Array<String>) {
    if (args.isEmpty()) {
      System.setProperty("DOC_BASE", "/home/take/src/FxJavadocViewer/12.zip")
    } else {
      System.setProperty("DOC_BASE", args[0])
    }
    Application.launch(App::class.java, *args)
  }

  class App : Application() {
    override fun start(stage: Stage) {
      val url = this.javaClass.getResource("/jp/rhino/take/MainPanel.fxml")
      //            println(url)
      val loader = FXMLLoader(url)
      val anchorPane: AnchorPane = loader.load()
      val mainPanel: MainPanel = loader.getController()

      stage.scene = Scene(anchorPane)
      stage.title = Main::class.qualifiedName
      stage.show()
    }
  }
}