package hu.szigyi.timelapse.ramping.conf

case class Configuration(timelapseRamping: TimelapseRamping)

case class TimelapseRamping(imagesConfig: ImagesConfig,
                            default: Default)

case class ImagesConfig(supportedFileExtensions: List[String])

case class Default(aperture: Option[BigDecimal],
                   exposureBias: Option[BigDecimal])