package jp.rhino.take

import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.TextField
import javafx.scene.web.WebView
import java.net.URL
import java.util.*

class MainPanel {
  @FXML
  private lateinit var resources: ResourceBundle
  @FXML
  private lateinit var location: URL
  @FXML
  private lateinit var _moduleList: ListView<ModuleInfo>
  @FXML
  private lateinit var _packageList: ListView<PackageInfo>
  @FXML
  private lateinit var _classList: ListView<ClassInfo>
  @FXML
  private lateinit var _constantValuesLabel: Label
  @FXML
  private lateinit var _deprecatedLabel: Label
  @FXML
  private lateinit var _webView: WebView
  @FXML
  private lateinit var _url: TextField

  @FXML
  fun initialize() {
    assert(
        _moduleList != null
    ) { "fx:id=\"_moduleList\" was not injected: check your FXML file 'MainPanel.fxml'." }
    assert(
        _packageList != null
    ) { "fx:id=\"_packageList\" was not injected: check your FXML file 'MainPanel.fxml'." }
    assert(_classList != null) { "fx:id=\"_classList\" was not injected: check your FXML file 'MainPanel.fxml'." }
    assert(
        _constantValuesLabel != null
    ) { "fx:id=\"_constantValuesLabel\" was not injected: check your FXML file 'MainPanel.fxml'." }
    assert(
        _deprecatedLabel != null
    ) { "fx:id=\"_deprecatedLabel\" was not injected: check your FXML file 'MainPanel.fxml'." }
    assert(_webView != null) { "fx:id=\"_webView\" was not injected: check your FXML file 'MainPanel.fxml'." }
    assert(_url != null) { "fx:id=\"_url\" was not injected: check your FXML file 'MainPanel.fxml'." }

    //

    //        _constantValuesLabel.setOnMouseClicked { ev ->
    //            val url = "jar:file:/home/take/src/FxJavadocViewer/12.zip!/F18208_01/docs/api/constant-values.html"
    //            println(url)
    //            _webView.engine.load(url)
    //            println(_webView.engine.document)
    //        }
    //
    //        if(true) return

    //        val docBase = Paths.get("./xx-doc/F18208_01/docs/api/").toAbsolutePath().toString()
    //        val docBase = "jar:file:/home/take/src/FxJavadocViewer/12.zip!/F18208_01/docs/api"
    val javadocBase = JavadocBase("/home/take/src/FxJavadocViewer/12.zip")

    _constantValuesLabel.setOnMouseClicked { ev ->
      val url = javadocBase.url("constant-values.html")
      _webView.engine.load(url)
    }
    _deprecatedLabel.setOnMouseClicked { ev ->
      val url = javadocBase.url("deprecated-list.html")
      _webView.engine.load(url)
    }

    val elementList = ElementList(javadocBase)

    _moduleList.items.addAll(listOf(ModuleInfo.ALL) + elementList.moduleInfoList.list)

    _moduleList.selectionModel.selectedItemProperty().addListener { _, _, nv ->
      if (nv != null) {
        _packageList.items.setAll(listOf(PackageInfo.ALL) + elementList.packageInfoList.find(nv))
        //        val url = javadocBase.url(nv.name.name, "module-summary.html")
        val url = javadocBase.url(nv.path.toString())
        println("url: ${url}")
        _webView.engine.load(url.toString())
      }
    }

    _packageList.selectionModel.selectedItemProperty().addListener { _, _, nv ->
      if (nv != null) {

        _classList.items.setAll(elementList.classInfoList.find(nv))
        //        val url = javadocBase.url(moduleName.name.name, nv.name.name.replace('.', '/'), "package-summary.html")
        val url = javadocBase.url(nv.path.toString())
        println("url: ${url}")
        _webView.engine.load(url.toString())
      }
    }

    _classList.selectionModel.selectedItemProperty().addListener { _, _, nv ->
      if (nv != null) {
        val url = javadocBase.url(nv.path.toString())
        println("url: ${url}")
        _webView.engine.load(url.toString())
      }
    }
  }
}