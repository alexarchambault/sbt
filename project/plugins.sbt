scalaVersion := "2.12.7"
scalacOptions ++= Seq("-feature", "-language:postfixOps")

addSbtPlugin("org.scala-sbt"     % "sbt-houserules"  % "0.3.9")
addSbtPlugin("com.lucidchart"    % "sbt-scalafmt"    % "1.15")
addSbtPlugin("org.scala-sbt"     % "sbt-contraband"  % "0.4.1")
addSbtPlugin("de.heikoseeberger" % "sbt-header"      % "3.0.2")
addSbtPlugin("com.eed3si9n"      % "sbt-buildinfo"   % "0.8.0")
addSbtPlugin(("com.lightbend"    % "sbt-whitesource" % "0.1.9").exclude("org.boris", "pecoff4j"))
