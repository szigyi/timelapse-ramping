package hu.szigyi.timelapse.ramping.factory

import com.typesafe.scalalogging.LazyLogging
import hu.szigyi.timelapse.ramping.conf.Configuration

trait ConfigurationFactory extends LazyLogging {

  val config = pureconfig.loadConfig[Configuration] match {
    case Right(c) => c
    case Left(error) => {
      error.toList.foreach(e => logger.error(e.toString))
      throw new RuntimeException("Unable to parse Configuration file into their classes!")
    }
  }
  val timelapseRampingConfig = config.timelapseRamping
  val imagesConfig = timelapseRampingConfig.imagesConfig
}
