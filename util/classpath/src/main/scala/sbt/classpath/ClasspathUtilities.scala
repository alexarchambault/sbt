/* sbt -- Simple Build Tool
 * Copyright 2008, 2009, 2010 Mark Harrah
 */
package sbt
package classpath

import java.io.File
import java.net.{ URI, URL, URLClassLoader }

object ClasspathUtilities {
  def toLoader(paths: Seq[File], parent: ClassLoader, resourceMap: Map[String, String], nativeTemp: File): ClassLoader =
    new URLClassLoader(Path.toURLs(paths), parent) with RawResources with NativeCopyLoader {
      override def resources = resourceMap
      override val config = new NativeCopyConfig(nativeTemp, paths, javaLibraryPaths)
      override def toString =
        s"""|URLClassLoader with NativeCopyLoader with RawResources(
            |  urls = $paths,
            |  parent = $parent,
            |  resourceMap = ${resourceMap.keySet},
            |  nativeTemp = $nativeTemp
            |)""".stripMargin
    }

  def javaLibraryPaths: Seq[File] = IO.parseClasspath(System.getProperty("java.library.path"))

  lazy val rootLoader =
    {
      def parent(loader: ClassLoader): ClassLoader =
        {
          val p = loader.getParent
          if (p eq null) loader else parent(p)
        }
      val systemLoader = ClassLoader.getSystemClassLoader
      if (systemLoader ne null) parent(systemLoader)
      else parent(getClass.getClassLoader)
    }
  final val AppClassPath = "app.class.path"
  final val BootClassPath = "boot.class.path"

  def createClasspathResources(appPaths: Seq[File], bootPaths: Seq[File]): Map[String, String] =
    {
      def make(name: String, paths: Seq[File]) = name -> Path.makeString(paths)
      Map(make(AppClassPath, appPaths), make(BootClassPath, bootPaths))
    }

  private[sbt] def filterByClasspath(classpath: Seq[File], rootLoader: ClassLoader, loader: ClassLoader): ClassLoader =
    new ClasspathFilter(loader, rootLoader, classpath.toSet)

  def makeLoader(classpath: Seq[File], rootLoader: ClassLoader, loader: ClassLoader, bootPath: Seq[File], nativeTemp: File): ClassLoader =
    filterByClasspath(classpath, rootLoader, toLoader(classpath, loader, createClasspathResources(classpath, bootPath), nativeTemp))

  private[sbt] def printSource(c: Class[_]) =
    println(c.getName + " loader=" + c.getClassLoader + " location=" + IO.classLocationFile(c))

  def isArchive(file: File): Boolean = isArchive(file, contentFallback = false)

  def isArchive(file: File, contentFallback: Boolean): Boolean =
    file.isFile && (isArchiveName(file.getName) || (contentFallback && hasZipContent(file)))

  def isArchiveName(fileName: String) = fileName.endsWith(".jar") || fileName.endsWith(".zip")

  def hasZipContent(file: File): Boolean = try {
    Using.fileInputStream(file) { in =>
      (in.read() == 0x50) &&
        (in.read() == 0x4b) &&
        (in.read() == 0x03) &&
        (in.read() == 0x04)
    }
  } catch { case e: Exception => false }

  /** Returns all entries in 'classpath' that correspond to a compiler plugin.*/
  private[sbt] def compilerPlugins(classpath: Seq[File]): Iterable[File] =
    {
      import collection.JavaConversions._
      val loader = new URLClassLoader(Path.toURLs(classpath))
      loader.getResources("scalac-plugin.xml").toList.flatMap(asFile(true))
    }
  /** Converts the given URL to a File.  If the URL is for an entry in a jar, the File for the jar is returned. */
  private[sbt] def asFile(url: URL): List[File] = asFile(false)(url)
  private[sbt] def asFile(jarOnly: Boolean)(url: URL): List[File] =
    {
      try {
        url.getProtocol match {
          case "file" if !jarOnly => IO.toFile(url) :: Nil
          case "jar" =>
            val path = url.getPath
            val end = path.indexOf('!')
            new File(new URI(if (end == -1) path else path.substring(0, end))) :: Nil
          case _ => Nil
        }
      } catch { case e: Exception => Nil }
    }
}
