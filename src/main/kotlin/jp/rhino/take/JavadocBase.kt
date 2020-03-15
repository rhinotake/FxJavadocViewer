package jp.rhino.take

import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths

class JavadocBase(val docBaseName: String) {
  val baseUrl: URL = if (docBaseName.matches("""^(http|https|file|jar:file):""".toRegex())) {
    URL(docBaseName)
  } else {
    val path = Paths.get(docBaseName)
    if (Files.isDirectory(path)) {
      path.toAbsolutePath().toFile().toURI().toURL()
    } else if (Files.isRegularFile(path)) {
      URL("jar:file:${path.toAbsolutePath()}!/F18208_01/docs/api/")
    } else {
      throw Exception(docBaseName)
    }
  }

  fun mkUrl(vararg path: String): String =
      "${baseUrl}/${arrayOf(*path).joinToString("/")}"
          .replace(System.getProperty("file.separator"), "/")
          .replace("//+".toRegex(), "/")

}