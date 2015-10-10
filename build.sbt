name := "HelloSlick3"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

resolvers ++= Seq(
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
  "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
)

libraryDependencies ++= Seq(cache, ws, evolutions
  , "com.h2database" % "h2" % "1.4.187"
  , "ch.qos.logback" % "logback-classic" % "1.1.3"
  , "ch.qos.logback" % "logback-core" % "1.1.3"
  , "com.typesafe.play" %% "play-slick" % "1.1.0"
  , "com.typesafe.play" %% "play-slick-evolutions" % "1.1.0"
  , "com.typesafe.play" %% "play-specs2" % "2.4.3" % "test"
)

javaOptions in Test += "-Dconfig.file=conf/application.test.conf"

scalacOptions in Test ++= Seq("-Yrangepos")

parallelExecution in Test := false

fork in Test := true

routesGenerator := InjectedRoutesGenerator
