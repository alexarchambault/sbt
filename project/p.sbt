libraryDependencies ++= Seq(
  "net.databinder" %% "dispatch-http" % "0.8.9",
  "org.jsoup" % "jsoup" % "1.7.1"
)

addSbtPlugin("com.typesafe.sbt" % "sbt-site" % "0.7.1")

resolvers += "jgit-repo" at "http://download.eclipse.org/jgit/maven"

addSbtPlugin("com.typesafe.sbt" % "sbt-ghpages" % "0.5.1")

addSbtPlugin("com.typesafe.sbt" % "sbt-scalariform" % "1.3.0")

addSbtPlugin("com.eed3si9n" % "sbt-doge" % "0.1.1")

addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "0.2.1")

addSbtPlugin("com.typesafe.sbt" % "sbt-pgp" % "0.8.3")

addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "0.6.2")
