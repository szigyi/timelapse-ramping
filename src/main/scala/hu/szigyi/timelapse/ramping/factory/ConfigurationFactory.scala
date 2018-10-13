package hu.szigyi.timelapse.ramping.factory

import com.typesafe.scalalogging.LazyLogging
import hu.szigyi.timelapse.ramping.conf.{ApplicationConfiguration, ReferenceConfiguration}

trait ConfigurationFactory extends LazyLogging {

  val refConfig = pureconfig.loadConfigOrThrow[ReferenceConfiguration]
  val metadataConfig = refConfig.metadata
  val versionConfig = metadataConfig.version


  val appConfig = pureconfig.loadConfigOrThrow[ApplicationConfiguration]
  val timelapseRampingConfig = appConfig.timelapseRamping
  val imagesConfig = timelapseRampingConfig.imagesConfig
  val defaultConfig = timelapseRampingConfig.default
  val modesConfig = timelapseRampingConfig.modes
}
