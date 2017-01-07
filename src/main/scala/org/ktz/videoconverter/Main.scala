package org.ktz.videoconverter

import com.typesafe.config.{Config, ConfigFactory}
import org.ktz.videoconverter.core.video.VideoConverter
import better.files._
import com.twitter.util.Await

/**
  * Created by ktz on 17. 1. 8.
  */
object Main extends App {
  val config = ConfigFactory.load

  val videoConverter: VideoConverter = new VideoConverter(ConfigFactory.load)

  println(videoConverter.getFFmpegVersion)

  getHomePath(config).listRecursively.foreach{ file =>
    if(VideoConverter.isContainerSupport(file)) Await.result(videoConverter.convertToMp4(file))
  }

  private def getHomePath(config: Config): File = config.getString("video.homePath").toFile
}
