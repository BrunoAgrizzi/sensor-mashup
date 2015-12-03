name := """play-californium"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  javaJdbc,
  javaJpa,
  javaEbean,
  cache,
  javaWs
)

libraryDependencies += "org.eclipse.californium" % "californium-core" % "1.0.0-M3"

javacOptions ++= Seq("-source", "1.7", "-target", "1.7")