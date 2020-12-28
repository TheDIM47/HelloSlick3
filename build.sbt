name := "HelloSlick3"
version := "1.1-SNAPSHOT"
scalaVersion := "2.13.4"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies ++= Seq(guice, caffeine, ws, evolutions
  , "com.h2database" % "h2" % "1.4.200"
  , "ch.qos.logback" % "logback-classic" % "1.2.3"
  , "ch.qos.logback" % "logback-core" % "1.2.3"
  , "com.typesafe.play" %% "play-slick" % "5.0.0"
  , "com.typesafe.play" %% "play-slick-evolutions" % "5.0.0"
  , "com.typesafe.play" %% "play-specs2" % "2.8.7" % "test"
)

javaOptions in Test += "-Dconfig.file=conf/application.test.conf"
scalacOptions in Test ++= Seq("-Yrangepos")
parallelExecution in Test := false
fork in Test := true
routesGenerator := InjectedRoutesGenerator
