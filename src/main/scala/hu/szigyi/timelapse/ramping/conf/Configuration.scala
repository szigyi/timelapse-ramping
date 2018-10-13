package hu.szigyi.timelapse.ramping.conf

import pureconfig.module.enum._
import enum.Enum

// reference.conf
case class ReferenceConfiguration(metadata: MetadataConfig)

case class MetadataConfig(appName: String,
                          version: VersionConfig)

case class VersionConfig(major: Int, minor: Int, micro: Int)


// application.conf
case class ApplicationConfiguration(timelapseRamping: TimelapseRampingConfig)

case class TimelapseRampingConfig(imagesConfig: ImagesConfig,
                                  default: DefaultConfig,
                                  modes: Modes)

case class ImagesConfig(supportedFileExtensions: List[String])

case class DefaultConfig(aperture: Option[BigDecimal],
                         exposure: BigDecimal,
                         rampWhiteBalance: Boolean)

case class Modes(mode: String, reportOnly: Boolean)

//sealed trait Mode
//
//object Mode {
//
//  case object Mirror extends Mode
//  case object MirrorAndSqueeze extends Mode
//  case object AverageWindow extends Mode
//  case object Interpolate extends Mode
//
//  val EnumInstance: Enum[Mode] = Enum.derived[Mode]
//}
