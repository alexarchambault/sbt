import Util._
import Dependencies._

lazy val safeUnitTests = taskKey[Unit]("Known working tests (for both 2.10 and 2.11)")

def customCommands: Seq[Setting[_]] = Seq(
  commands += Command.command("setupBuildScala211") { state =>
    s"""set scalaVersion in ThisBuild := "$scala211" """ ::
      state
  },
  // This is invoked by Travis
  commands += Command.command("checkBuildScala211") { state =>
    s"++ $scala211" ::
      // First compile everything before attempting to test
      "all compile test:compile" ::
      // Now run known working tests.
      safeUnitTests.key.label ::
      state
  }
)

def commonSettings: Seq[Setting[_]] = Seq(
  organization := "org.scala-sbt",
  version := "0.13.8-SNAPSHOT",
  scalaVersion in ThisBuild := "2.11.4",
  publishArtifact in packageDoc := false,
  publishMavenStyle := false,
  componentID := None,
  crossPaths := false,
  resolvers += Resolver.typesafeIvyRepo("releases"),
  concurrentRestrictions in Global += Util.testExclusiveRestriction,
  testOptions += Tests.Argument(TestFrameworks.ScalaCheck, "-w", "1"),
  javacOptions in compile ++= Seq("-target", "6", "-source", "6", "-Xlint", "-Xlint:-serial"),
  incOptions := incOptions.value.withNameHashing(true)
)

def minimalSettings: Seq[Setting[_]] =
  commonSettings ++ customCommands ++ Status.settings ++
  publishPomSettings ++ Release.javaVersionCheckSettings

def baseSettings: Seq[Setting[_]] =
  minimalSettings ++ Seq(projectComponent) ++ baseScalacOptions ++ Licensed.settings ++ Formatting.settings


// Path, IO (formerly FileUtilities), NameFilter and other I/O utility classes
lazy val ioProj = (project in file("util") / "io").
  settings(baseSettings ++ testDependencies ++ Util.crossBuild: _*).
  settings(
    name := "IO",
    libraryDependencies += scalaCompiler.value % Test,
    crossScalaVersions := Seq(scala210, scala211)
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

lazy val nonRoots =
  Seq(ioProj, ivyProj)
    .map(p => p.copy(configurations = (p.configurations.filter(_ != Provided)) :+ config("provided").intransitive))
    .map(p => LocalProject(p.id))

lazy val root: Project = (project in file(".")).
  aggregate(nonRoots: _*).
  settings(minimalSettings ++ Util.publishPomSettings ++ Formatting.sbtFilesSettings: _*)
