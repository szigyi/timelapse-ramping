package hu.szigyi.timelapse.ramping.conf

case class Configuration(timelapseRamping: TimelapseRamping)

case class TimelapseRamping(imagesConfig: ImagesConfig,
                            default: Default)

case class ImagesConfig(supportedFileExtensions: List[String])

case class Default(manualLens: Boolean,
                   aperture: BigDecimal)