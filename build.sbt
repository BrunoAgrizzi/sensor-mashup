name := """sensor-mashup"""

version := "0.1.0"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  javaJdbc,
  javaJpa,
  javaEbean,
  cache,
  javaWs,
  "org.eclipse.californium" % "californium-core" % "1.0.0-M3"
)

//docker setup
packageName in Docker := "agrizzi/sensor-mashup"

version in Docker := "0.1.0"

maintainer in Docker := "Bruno Agrizzi"

dockerExposedPorts in Docker := Seq(9000, 9443,5683)

dockerExposedVolumes in Docker := Seq("/opt/docker/logs")