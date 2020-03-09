package jp.rhino.take

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.nio.file.Path
import java.nio.file.Paths

/**
 */
data class ModuleName(val name: String) {
  override fun toString() = name
}

/**
 */
data class PackageName(val name: String) {
  override fun toString() = name
}

/**
 */
enum class ClassTypes {
  ALL_TYPES,
  INTERFACES,
  CLASSES,
  EXCEPTIONS,
  ENUMS,
}

/**
 */
data class ClassName(val name: String) {
  override fun toString() = name
}

/**
 */
data class ModuleInfo(val name: ModuleName, val path: Path) :
    Comparable<ModuleInfo> {
  override fun compareTo(other: ModuleInfo): Int =
      name.name.compareTo(other.name.name)

  override fun toString() = name.toString()

  companion object {
    val ALL = ModuleInfo(name = ModuleName("ALL MODULES"), path = Paths.get(""))
  }
}

/**
 */
data class PackageInfo(val name: PackageName, val path: Path, val moduleInfo: ModuleInfo) :
    Comparable<PackageInfo> {
  override fun compareTo(other: PackageInfo): Int =
      name.name.compareTo(other.name.name)

  override fun toString() = name.toString()

  companion object {
    val ALL = PackageInfo(name = PackageName("ALL PACKAGES"), path = Paths.get(""), moduleInfo = ModuleInfo.ALL)
  }
}

/**
 */
data class ClassInfo(val name: ClassName, val path: Path, val packageInfo: PackageInfo) :
    Comparable<ClassInfo> {
  override fun compareTo(other: ClassInfo): Int =
      name.name.compareTo(other.name.name).let { it ->
        if (it == 0) path.toString().compareTo(other.path.toString())
        else it
      }

  override fun toString() = name.toString()
}

/**
 */
object Utils {
  /**
   */
  fun createDocument(url: String): Document {
    val html: String =
        URL(url).openStream().use { inputStream ->
          BufferedReader(InputStreamReader(inputStream)).use { br ->
            br.readLines().joinToString("\n")
          }
        }

    return Jsoup.parse(html)
  }
}

/**
 */
data class ModuleInfoList(val list: List<ModuleInfo>) {
  companion object {
    /**
     */
    fun mk(javadocBase: JavadocBase): ModuleInfoList {
      val list = mutableListOf<ModuleInfo>()

      javadocBase.url("index.html").let { allpackagesIndex ->
        val document = Utils.createDocument(allpackagesIndex)
        document.select("th.colFirst").forEach { td ->
          td.select("a").forEach { a ->
            list += ModuleInfo(name = ModuleName(a.text()), path = Paths.get(a.attr("href")))
          }
        }
      }

      return ModuleInfoList(list = list.toList().sorted())
    }
  }
}

/**
 */
data class PackageInfoList(val list: List<PackageInfo>) {

  /**
   */
  fun find(modluleInfo: ModuleInfo): List<PackageInfo> =
      if (modluleInfo == ModuleInfo.ALL) {
        list
      } else {
        list.filter { it.moduleInfo == modluleInfo }
      }

  /**
   */
  companion object {
    /**
     */
    fun mk(javadocBase: JavadocBase, moduleInfoList: ModuleInfoList): PackageInfoList {
      val list = mutableListOf<PackageInfo>()

      javadocBase.url("allpackages-index.html").let { allpackagesIndex ->
        val document = Utils.createDocument(allpackagesIndex)
        document.select("th.colFirst").forEach { td ->
          td.select("a").forEach { a ->
            val path = Paths.get(a.attr("href"))
            val moduleInfo = moduleInfoList.list.filter { mod -> path.startsWith(mod.path.parent) }.firstOrNull()
            if (moduleInfo != null) {
              list += PackageInfo(name = PackageName(a.text()), path = path, moduleInfo = moduleInfo)
            }
          }
        }
      }

      return PackageInfoList(list = list.toList().sorted())
    }
  }
}


/**
 */
data class ClassInfoList(val list: List<ClassInfo>) {
  /**
   */
  fun find(packageInfo: PackageInfo): List<ClassInfo> =
      if (packageInfo == PackageInfo.ALL) {
        list
      } else {
        list.filter { it.packageInfo == packageInfo }
      }

  /**
   */
  companion object {
    /**
     */
    fun mk(javadocBase: JavadocBase, packageInfoList: PackageInfoList): ClassInfoList {
      val list = mutableListOf<ClassInfo>()

      javadocBase.url("allclasses-index.html").let { allClasssIndex ->
        val document = Utils.createDocument(allClasssIndex)
        document.select("td.colFirst").forEach { td ->
          td.select("a").forEach { a ->
            val path = Paths.get(a.attr("href"))
            val packageInfo = packageInfoList.list.filter { pac -> path.startsWith(pac.path.parent) }.firstOrNull()
            if (packageInfo != null) {
              list += ClassInfo(name = ClassName(a.text()), path = path, packageInfo = packageInfo)
            }
          }
        }
      }

      return ClassInfoList(list = list.toList().sorted())
    }
  }
}


/**
 */
class ElementList(javadocBase: JavadocBase) {
  val moduleInfoList: ModuleInfoList = ModuleInfoList.mk(javadocBase)
  val packageInfoList: PackageInfoList = PackageInfoList.mk(javadocBase, moduleInfoList)
  val classInfoList: ClassInfoList = ClassInfoList.mk(javadocBase, packageInfoList)
}