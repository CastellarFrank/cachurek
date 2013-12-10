name := "cachurek"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "mysql" % "mysql-connector-java" % "5.1.27",
  "com.typesafe" %% "play-plugins-mailer" % "2.1.0",
  "net.tanesha.recaptcha4j" % "recaptcha4j" % "0.0.7"
)  

play.Project.playScalaSettings
