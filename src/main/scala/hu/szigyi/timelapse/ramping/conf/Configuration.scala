package hu.szigyi.timelapse.ramping.conf

case class Configuration(timelapseRamping: TimelapseRamping)

case class TimelapseRamping(imagesConfig: ImagesConfig)

case class ImagesConfig(suppoertedFileExtensions: List[String])