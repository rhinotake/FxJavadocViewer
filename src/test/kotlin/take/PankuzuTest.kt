package take

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.Stage
import jp.rhino.take.LabelWithListView
import java.util.*

object PankuzuTest {
  @JvmStatic
  fun main(args: Array<String>) {
    Application.launch(App::class.java, *args)
  }
}

class App : Application() {
  override fun start(stage: Stage) {
    val aaa = LabelWithListView<String>(Label("aaa"))
    repeat(100) { index ->
      aaa.items.addAll("xxx-" + Math.random())
    }

    val bbb = LabelWithListView<String>(Label("bbb"))
    bbb.items.setAll((0..20).map { UUID.randomUUID().toString().substring(0, 10) })

    stage.scene = Scene(VBox(
        HBox(aaa.label, Label(" >> "), bbb.label, Label(" >> ")),
        TextArea()))
    stage.show()
  }
}
