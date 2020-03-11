package jp.rhino.take

import javafx.fxml.FXML
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.TextField
import javafx.scene.web.WebView
import java.net.URL
import java.util.*

/**
 */
class MainPanel {
  @FXML
  private lateinit var resources: ResourceBundle

  @FXML
  private lateinit var location: URL

  @FXML
  private lateinit var _modulesList: ListView<ModuleInfo>

  @FXML
  private lateinit var _packagesList: ListView<PackageInfo>

  @FXML
  private lateinit var _classesList: ListView<ClassInfo>

  @FXML
  private lateinit var _constantValuesLabel: Label

  @FXML
  private lateinit var _deprecatedLabel: Label

  @FXML
  private lateinit var _webView: WebView

  @FXML
  private lateinit var _url: TextField

  @FXML
  private lateinit var _modulesComboBox: ComboBox<ModuleInfo>

  @FXML
  private lateinit var _packagesComboBox: ComboBox<PackageInfo>

  @FXML
  private lateinit var _typesComboBox: ComboBox<ClassTypeName>

  @FXML
  private lateinit var _classesComboBox: ComboBox<ClassInfo>

  @FXML
  fun initialize() {

    //    assert(
    //        _modulesList != null
    //    ) { "fx:id=\"_moduleList\" was not injected: check your FXML file 'MainPanel.fxml'." }
    //    assert(
    //        _packagesList != null
    //    ) { "fx:id=\"_packageList\" was not injected: check your FXML file 'MainPanel.fxml'." }
    //    assert(_classesList != null) { "fx:id=\"_classList\" was not injected: check your FXML file 'MainPanel.fxml'." }
    //    assert(
    //        _constantValuesLabel != null
    //    ) { "fx:id=\"_constantValuesLabel\" was not injected: check your FXML file 'MainPanel.fxml'." }
    //    assert(
    //        _deprecatedLabel != null
    //    ) { "fx:id=\"_deprecatedLabel\" was not injected: check your FXML file 'MainPanel.fxml'." }
    //    assert(_webView != null) { "fx:id=\"_webView\" was not injected: check your FXML file 'MainPanel.fxml'." }
    //    assert(_url != null) { "fx:id=\"_url\" was not injected: check your FXML file 'MainPanel.fxml'." }

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
    val javadocBase = JavadocBase(System.getProperty("DOC_BASE"))

    _constantValuesLabel.setOnMouseClicked { ev ->
      val url = javadocBase.url("constant-values.html")
      _webView.engine.load(url)
    }
    _deprecatedLabel.setOnMouseClicked { ev ->
      val url = javadocBase.url("deprecated-list.html")
      _webView.engine.load(url)
    }

    val elementList = ElementList(javadocBase)

    _modulesList.items.addAll(listOf(ModuleInfo.ALL) + elementList.moduleInfoList.list)

    _modulesList.selectionModel.selectedItemProperty().addListener { _, _, nv ->
      if (nv != null) {
        _packagesList.items.setAll(listOf(PackageInfo.ALL) + elementList.packageInfoList.find(nv))
        //        val url = javadocBase.url(nv.name.name, "module-summary.html")
        val url = javadocBase.url(nv.path.toString())
        println("url: ${url}")
        _webView.engine.load(url.toString())
      }
    }

    _packagesList.selectionModel.selectedItemProperty().addListener { _, _, nv ->
      if (nv != null) {

        _classesList.items.setAll(elementList.classInfoList.find(nv))
        _typesComboBox.items.setAll(listOf(ClassTypeName.ALL) + elementList.classInfoList.classTypeNameList(nv))
        //        val url = javadocBase.url(moduleName.name.name, nv.name.name.replace('.', '/'), "package-summary.html")
        val url = javadocBase.url(nv.path.toString())
        println("url: ${url}")
        _webView.engine.load(url.toString())
      }
    }

    _classesList.selectionModel.selectedItemProperty().addListener { _, _, nv ->
      if (nv != null) {
        val url = javadocBase.url(nv.path.toString())
        println("url: ${url}")
        _webView.engine.load(url.toString())
      }
    }

    //
    _modulesComboBox.itemsProperty().bind(_modulesList.itemsProperty())
    _packagesComboBox.itemsProperty().bind(_packagesList.itemsProperty())
//    _typesComboBox.items.setAll(Arrays.asList(*ClassTypes.values()))
    _classesComboBox.itemsProperty().bind(_classesList.itemsProperty())

    _modulesComboBox.selectionModel.selectedItemProperty().addListener { _, _, nv ->
      _modulesList.selectionModel.select(nv)
      if(_modulesComboBox.isFocused){
        _modulesList.scrollTo(nv)
      }
    }
    _modulesList.selectionModel.selectedItemProperty().addListener { _, _, nv -> _modulesComboBox.selectionModel.select(nv) }

    _packagesComboBox.selectionModel.selectedItemProperty().addListener { _, _, nv ->
      _packagesList.selectionModel.select(nv)
      if(_packagesComboBox.isFocused){
        _packagesList.scrollTo(nv)
      }
    }
    _packagesList.selectionModel.selectedItemProperty().addListener { _, _, nv -> _packagesComboBox.selectionModel.select(nv) }

    _classesComboBox.selectionModel.selectedItemProperty().addListener { _, _, nv ->
      _classesList.selectionModel.select(nv)
      if(_classesComboBox.isFocused) {
        _classesList.scrollTo(nv)
      }
    }
    _classesList.selectionModel.selectedItemProperty().addListener { _, _, nv -> _classesComboBox.selectionModel.select(nv) }

    //
    _webView.engine.locationProperty().addListener{_,_,nv ->
      _url.text = nv
    }
  }
}