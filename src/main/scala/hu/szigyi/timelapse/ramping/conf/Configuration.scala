package hu.szigyi.timelapse.ramping.conf

// reference.conf
case class ReferenceConfiguration(metadata: MetadataConfig)

case class MetadataConfig(appName: String,
                          version: VersionConfig)

case class VersionConfig(major: Int, minor: Int, micro: Int)


// application.conf
case class ApplicationConfiguration(timelapseRamping: TimelapseRampingConfig)

case class TimelapseRampingConfig(imagesConfig: ImagesConfig,
                                  default: DefaultConfig)

case class ImagesConfig(supportedFileExtensions: List[String])

case class DefaultConfig(aperture: Option[BigDecimal],
                         exposure: BigDecimal)