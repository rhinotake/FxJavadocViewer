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

///**
// */
//enum class ClassTypes {
//  ALL_TYPES,
//  INTERFACES,
//  CLASSES,
//  EXCEPTIONS,
//  ENUMS,
//}

/**
 */
data class ClassTypeName(val name: String) {
  override fun toString() = name

  companion object {
    val ALL = ClassTypeName(name = "ALL TYPES")
  }
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
data class ClassInfo(val name: ClassName, val path: Path, val packageInfo: PackageInfo, val classTypeName: ClassTypeName) :
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

      return ModuleInfoList(list = list.toList())
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

      return PackageInfoList(list = list.toList())
    }
  }
}

/**
 */
data class ClassTypeNameList(val list: List<ClassTypeName>)

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
  fun find(packageInfo: PackageInfo, classTypeName: ClassTypeName): List<ClassInfo> =
      if (classTypeName == ClassTypeName.ALL) {
        find(packageInfo)
      } else {
        find(packageInfo).filter { it.classTypeName == classTypeName }
      }

  /**
   */
  fun classTypeNameList(packageInfo: PackageInfo): List<ClassTypeName> =
      find(packageInfo).map { it.classTypeName }.distinct()

  /**
   */
  companion object {
    /**
     */
    fun mk(javadocBase: JavadocBase, packageInfoList: PackageInfoList): ClassInfoList {
      val list = mutableListOf<ClassInfo>()

      packageInfoList.list.forEach { packageInfo ->
        javadocBase.url(packageInfo.path.toString()).let { xxx ->
          val document = Utils.createDocument(xxx)

          val typeSummaryList = document.select("div.typeSummary")
          //          println(typeSummaryList.size)
          typeSummaryList.take(99).forEach { typeSummary ->
            val ths = typeSummary.select("table tr > th.colFirst")
            val classTypeName = ClassTypeName(name = ths.firstOrNull()?.text() ?: "?")
            ths.take(1).forEach { th ->
              list +=
                  ClassInfo(
                      name = ClassName(" --- ${classTypeName} ---"),
                      path = Paths.get(""),
                      packageInfo = packageInfo,
                      classTypeName = classTypeName)
            }
            ths.drop(1).forEach { th ->
              th.selectFirst("a")?.let { anchor ->
                list +=
                    ClassInfo(
                        name = ClassName(th.text()),
                        path = packageInfo.path.parent.resolve(anchor.attr("href")),
                        packageInfo = packageInfo,
                        classTypeName = classTypeName)
              }
            }
          }
        }
      }

      return ClassInfoList(list = list.toList())
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