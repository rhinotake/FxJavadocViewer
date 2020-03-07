package jp.rhino.take

import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.TextField
import javafx.scene.web.WebView
import java.net.URL
import java.nio.file.Paths
import java.util.*

class MainPanel {
    @FXML
    private lateinit var resources: ResourceBundle
    @FXML
    private lateinit var location: URL
    @FXML
    private lateinit var _moduleList: ListView<String>
    @FXML
    private lateinit var _packageList: ListView<String>
    @FXML
    private lateinit var _classList: ListView<String>
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
        val docBase = Paths.get("./xx-doc/F18208_01/docs/api/")

        _constantValuesLabel.setOnMouseClicked { ev ->
            val url = docBase.resolve("constant-values.html").toFile().toURI().toURL()
            _webView.engine.load(url.toString())
        }
        _deprecatedLabel.setOnMouseClicked { ev ->
            val url = docBase.resolve("deprecated-list.html").toFile().toURI().toURL()
            _webView.engine.load(url.toString())
        }

        val elementList = ElementList(docBase.resolve("element-list").toFile().toURI().toURL())

        _moduleList.items.addAll(elementList.moduleList())

        _moduleList.selectionModel.selectedItemProperty().addListener { _, _, nv ->
            _packageList.items.setAll(elementList.packageList(nv))
            val url =
                docBase
                    .resolve(nv)
                    .resolve("module-summary.html").toFile().toURI().toURL()
            println("url: ${url}")
            _webView.engine.load(url.toString())
        }

        _packageList.selectionModel.selectedItemProperty().addListener { _, _, nv ->
            val url =
                docBase
                    .resolve(_moduleList.selectionModel.selectedItem)
                    .resolve(nv.replace('.', '/'))
                    .resolve("package-summary.html").toFile().toURI().toURL()
            println("url: ${url}")
            _webView.engine.load(url.toString())
        }
    }
}