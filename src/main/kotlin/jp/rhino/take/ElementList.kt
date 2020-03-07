package jp.rhino.take

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL

class ElementList(url: URL) {

    private val _elemantMap = mutableMapOf<String, MutableList<String>>()

    fun moduleList(): List<String>
         = _elemantMap.keys.sorted()

    fun packageList(moduleName:String): List<String> =
        if(moduleName.isEmpty()) {
            _elemantMap.values.flatten().sorted()
        } else {
            _elemantMap[moduleName]?.toList()?.sorted() ?: emptyList()
        }


    init {
        url.openStream().use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { br ->
                val lines = br.readLines()
                var moduleName: String = ""
                lines.forEach { line ->
                    if (line.startsWith("module:")) {
                        moduleName = line.replace("^module:".toRegex(), "")
                        _elemantMap[moduleName] = mutableListOf()
                    } else {
                        _elemantMap[moduleName]?.add(line)
                    }
                }
            }
        }
    }
}