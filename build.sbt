name := """app_slick"""
organization := "com.harrison"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.8"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-slick" % "4.0.2",
  "com.typesafe.play" %% "play-slick-evolutions" % "4.0.2",
  "com.microsoft.sqlserver" % "mssql-jdbc" % "7.2.2.jre8", // https://mvnrepository.com/artifact/com.microsoft.sqlserver/mssql-jdbc
//  "com.netaporter" %% "scala-uri" % "0.4.16",
//  "com.pauldijou" %% "jwt-core" % "2.0.0",
//  "io.swagger" %% "swagger-play2" % "1.7.1",
  "net.logstash.logback" % "logstash-logback-encoder" % "6.0",
)
libraryDependencies += "com.typesafe.slick" %% "slick" % "3.3.2"




