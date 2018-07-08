package hu.szigyi.timelapse.ramping.factory

import com.typesafe.scalalogging.LazyLogging
import hu.szigyi.timelapse.ramping.cli.CLI
import hu.szigyi.timelapse.ramping.fs.{FsUtil, Reader}
import hu.szigyi.timelapse.ramping.service.XmpService

trait ComponentFactory extends LazyLogging with ConfigurationFactory {

  val fsUtil = FsUtil()
  val reader = Reader(imagesConfig, fsUtil)
  val cli = CLI()
  val xmpService = XmpService(cli, fsUtil, reader)
}
