package org.ktz.videoconverter.core.video

import better.files._
import com.twitter.util.Future
import com.typesafe.config.Config
import net.bramp.ffmpeg.builder.FFmpegBuilder
import net.bramp.ffmpeg.job.FFmpegJob
import net.bramp.ffmpeg.probe.FFmpegFormat
import net.bramp.ffmpeg.{ FFmpeg, FFmpegExecutor, FFprobe }

/**
  * Created by ktz on 17. 1. 8.
  */

class VideoConverter(config: Config, ffmpegPath: Option[String] = None, ffprobePath: Option[String] = None) {
  import scala.collection.JavaConversions._
  private implicit val validVideoExtension: List[String] = config.getStringList("video.supportContainer").toList
  private val (mFFMpeg, mFFProbe) = initFFmpeg(ffmpegPath, ffprobePath)
  private val mFFmpegExecutor = new FFmpegExecutor(mFFMpeg, mFFProbe)

  def convertToMp4(fileToConvert: File, outputFile: File): Future[Option[Unit]] =
    operateIfFileValid(fileToConvert, Future(buildFFmpegJob(fileToConvert.pathAsString, outputFile.pathAsString).run()))

  def convertToMp4(fileToConvert: File): Future[Option[Unit]] = {
    val output = fileToConvert.pathAsString.split('.').head
    convertToMp4(fileToConvert, (output ++ ".mp4").toFile)
  }

  def getMediaInfo(fileToRead: File): Future[Option[FFmpegFormat]] =
    operateIfFileValid(fileToRead, mFFProbe.probe(fileToRead.pathAsString).format)

  def getFFmpegVersion: String = mFFMpeg.version()

  private def operateIfFileValid[A](fileToOperate: File, operation: => A): Future[Option[A]] = Future {
    if (isValidFile(fileToOperate)) Some(operation)
    else None
  }

  private def buildFFmpegJob(fileToConvert: String, outputFile: String): FFmpegJob = {
    val builder: FFmpegBuilder =
      (new FFmpegBuilder)
        .addInput(fileToConvert)
        .overrideOutputFiles(true)
        .addOutput(outputFile)
        .setVideoCodec("libx264")
        .setAudioCodec("aac")
        .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL).done()

    mFFmpegExecutor.createJob(builder)
  }

  private def isValidFile(fileToCheck: File): Boolean =
    if (fileToCheck.notExists) {
      false
    } else
      VideoConverter.isContainerSupport(fileToCheck)

  private def initFFmpeg(ffmpegPath: Option[String], ffprobePath: Option[String]): (FFmpeg, FFprobe) = (ffmpegPath, ffprobePath) match {
    case (Some(mpegPath), Some(probePath)) => (new FFmpeg(mpegPath), new FFprobe(probePath))
    case (Some(mpegPath), None) => (new FFmpeg(mpegPath), new FFprobe())
    case (None, Some(probePath)) => (new FFmpeg(), new FFprobe(probePath))
    case (None, None) => (new FFmpeg(), new FFprobe())
  }
}

object VideoConverter {
  def isContainerSupport(fileToCheck: File)(implicit validVideoExtension: List[String]) = fileToCheck.extension(false, true, true) match {
    case Some(extension) => validVideoExtension.contains(extension)
    case None => false
  }
}