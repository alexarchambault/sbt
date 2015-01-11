import sbt._
import Keys._

object Dependencies {
  lazy val ivy = "org.scala-sbt.ivy" % "ivy" % "2.3.0-sbt-fccfbd44c9f64523b61398a0155784dcbaeae28f"
  lazy val jsch = "com.jcraft" % "jsch" % "0.1.46" intransitive ()
  lazy val json4sNative = "org.json4s" %% "json4s-native" % "3.2.10"
  lazy val jawnParser = "org.spire-math" %% "jawn-parser" % "0.6.0"
  lazy val jawnJson4s = "org.spire-math" %% "json4s-support" % "0.6.0"
  lazy val scalaCompiler = Def.setting { "org.scala-lang" % "scala-compiler" % scalaVersion.value }
  lazy val scalaCheck = "org.scalacheck" %% "scalacheck" % "1.11.4"
  lazy val specs2 = "org.specs2" %% "specs2" % "2.3.11"
  lazy val junit = "junit" % "junit" % "4.11"

  lazy val scalaLogging =
    Def.setting {
      scalaVersion.value match {
        case sv if (sv startsWith "2.9.") || (sv startsWith "2.10.") =>
          "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2"
        case _ =>
          "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0"
      }
    }
}
