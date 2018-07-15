package hu.szigyi.timelapse.ramping.factory

import com.typesafe.scalalogging.LazyLogging
import hu.szigyi.timelapse.ramping.conf.{ApplicationConfiguration, ReferenceConfiguration}

trait ConfigurationFactory extends LazyLogging {

  val refConfig = pureconfig.loadConfig[ReferenceConfiguration] match {
    case Right(c) => c
    case Left(error) => {
      error.toList.foreach(e => logger.error(e.toString))
      throw new RuntimeException("Unable to parse reference.conf file into their classes!")
    }
  }
  val metadataConfig = refConfig.metadata
  val versionConfig = metadataConfig.version


  val appConfig = pureconfig.loadConfig[ApplicationConfiguration] match {
    case Right(c) => c
    case Left(error) => {
      error.toList.foreach(e => logger.error(e.toString))
      throw new RuntimeException("Unable to parse application.conf file into their classes!")
    }
  }
  val timelapseRampingConfig = appConfig.timelapseRamping
  val imagesConfig = timelapseRampingConfig.imagesConfig
  val defaultConfig = timelapseRampingConfig.default
}
