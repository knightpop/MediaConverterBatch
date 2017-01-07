import sbt.Keys._

lazy val versions = new {
  val ffmpegWrapper = "0.6.1"
  val betterFiles = "2.16.0"
  val logback = "1.1.7"
  val twitter = "6.40.0"
  val typesafeConfig = "1.3.1"
  val scalaGuice = "4.1.0"
}

name := "VideoConverterBatch"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= {
  Seq(
    "net.bramp.ffmpeg" % "ffmpeg" % versions.ffmpegWrapper,
    "com.github.pathikrit" %% "better-files" % versions.betterFiles,
    "ch.qos.logback"  % "logback-classic" % versions.logback,
    "com.twitter" %% "util-collection" % versions.twitter,
    "com.typesafe" % "config" % versions.typesafeConfig,
    "net.codingwell" %% "scala-guice" % versions.scalaGuice
  )
}