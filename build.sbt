name := "cachurek"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "mysql" % "mysql-connector-java" % "5.1.27",
  "com.typesafe" %% "play-plugins-mailer" % "2.1.0"
)  

play.Project.playScalaSettings
