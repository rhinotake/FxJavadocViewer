package jp.rhino.take

import javafx.beans.property.ObjectProperty
import javafx.collections.ObservableList
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle

/**
 */
class LabelWithListView<A>(val label: Label) {

  val listView = ListView<A>()

  val items: ObservableList<A>
    get() = listView.items

  fun itemsProperty(): ObjectProperty<ObservableList<A>> = listView.itemsProperty()

  //  init {
  //    listView.selectionModel.selectedItemProperty().addListener { _, _, nv ->
  //      label.text = nv?.toString() ?: ""
  //    }
  //  }

  private val _stage: Stage = Stage().also { stage ->
    stage.initModality(Modality.NONE)
    stage.initStyle(StageStyle.UNDECORATED)

    listView.selectionModel.selectedItemProperty().addListener { _, _, nv ->
      //      label.text = nv.toString()
      label.text = nv?.toString() ?: "--"
      stage.hide()
    }

    //    listView.prefWidth = Region.USE_COMPUTED_SIZE
    //    listView.prefHeight = Region.USE_COMPUTED_SIZE

    stage.scene = Scene(listView)
    stage.focusedProperty().addListener { _, _, nv ->
      if (!nv) {
        stage.hide()
      }
    }
    stage.setOnShown { ev ->
      stage.requestFocus()
    }
  }

  init {
    label.setOnMouseClicked { ev ->
      if (ev.clickCount == 1) {
        val pos = label.localToScreen(label.boundsInLocal)
        _stage.x = pos.minX
        _stage.y = pos.minY
        _stage.show()
      }
    }
  }
}