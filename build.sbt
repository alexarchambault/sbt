import Util._
import Dependencies._

def commonSettings: Seq[Setting[_]] = Seq(
  organization := "com.github.alexarchambault.sbt",
  version := "0.13.8-SNAPSHOT",
  scalaVersion := "2.11.4",
  crossScalaVersions := Seq("2.10.4", "2.11.4"),
  publishArtifact in packageDoc := false,
  publishMavenStyle := false,
  resolvers += Resolver.typesafeIvyRepo("releases"),
  concurrentRestrictions in Global += Util.testExclusiveRestriction,
  testOptions += Tests.Argument(TestFrameworks.ScalaCheck, "-w", "1"),
  javacOptions in compile ++= Seq("-target", "6", "-source", "6", "-Xlint", "-Xlint:-serial"),
  incOptions := incOptions.value.withNameHashing(true)
)

def minimalSettings: Seq[Setting[_]] =
  commonSettings ++ Status.settings ++
  publishPomSettings

def baseSettings: Seq[Setting[_]] =
  minimalSettings ++ baseScalacOptions ++ Licensed.settings ++ Formatting.settings


// Path, IO (formerly FileUtilities), NameFilter and other I/O utility classes
lazy val ioProj = (project in file("util") / "io").
  settings(baseSettings ++ testDependencies ++ Util.crossBuild: _*).
  settings(
    name := "IO",
    libraryDependencies += scalaCompiler.value % Test
  )

// Apache Ivy integration
lazy val ivyProj = (project in file("ivy")).
  dependsOn(ioProj % "compile;test->test").
  settings(baseSettings: _*).
  settings(
    name := "Ivy",
    libraryDependencies ++= Seq(ivy, jsch, json4sNative, jawnParser, jawnJson4s),
    libraryDependencies += scalaLogging.value,
    testExclusive)

lazy val classpathProj = (project in file("util") / "classpath").
  dependsOn(ioProj).
  settings(baseSettings ++ testDependencies: _*).
  settings(
    name := "Classpath",
    libraryDependencies += scalaCompiler.value
  )

lazy val nonRoots =
  Seq(ioProj, ivyProj, classpathProj)
    .map(p => p.copy(configurations = (p.configurations.filter(_ != Provided)) :+ config("provided").intransitive))
    .map(p => LocalProject(p.id))

lazy val root: Project = (project in file(".")).
  aggregate(nonRoots: _*).
  settings(minimalSettings ++ Util.publishPomSettings ++ Formatting.sbtFilesSettings: _*)
