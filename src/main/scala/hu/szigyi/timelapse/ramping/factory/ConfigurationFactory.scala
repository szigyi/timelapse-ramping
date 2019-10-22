package hu.szigyi.timelapse.ramping.factory

import com.typesafe.scalalogging.LazyLogging
import hu.szigyi.timelapse.ramping.conf.{ApplicationConfiguration, ReferenceConfiguration}
import pureconfig.ConfigSource
import pureconfig.generic.auto._

trait ConfigurationFactory extends LazyLogging {

  val refConfig = ConfigSource.default.loadOrThrow[ReferenceConfiguration]
  val metadataConfig = refConfig.metadata
  val versionConfig = metadataConfig.version


  val appConfig = ConfigSource.default.loadOrThrow[ApplicationConfiguration]
  val timelapseRampingConfig = appConfig.timelapseRamping
  val imagesConfig = timelapseRampingConfig.imagesConfig
  val defaultConfig = timelapseRampingConfig.default
  val modesConfig = timelapseRampingConfig.modes
}
