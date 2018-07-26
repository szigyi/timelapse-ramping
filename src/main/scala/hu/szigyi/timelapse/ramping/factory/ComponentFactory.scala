package hu.szigyi.timelapse.ramping.factory

import com.typesafe.scalalogging.LazyLogging
import hu.szigyi.timelapse.ramping.Application
import hu.szigyi.timelapse.ramping.algo.{Equations, EVdifference, MirrorPrevious}
import hu.szigyi.timelapse.ramping.cli.CLI
import hu.szigyi.timelapse.ramping.io.{IOUtil, Reader, Writer}
import hu.szigyi.timelapse.ramping.xmp.{XmpParser, XmpService}

trait ComponentFactory extends LazyLogging with ConfigurationFactory {

  val ioUtil = IOUtil()
  val reader = Reader(ioUtil)
  val writer = Writer()
  val cli = CLI()

  private val equations = Equations()
  private val exposureAlgo = EVdifference(equations)
  val rampAlgo = MirrorPrevious(exposureAlgo)

  val xmpParser = XmpParser(defaultConfig)
  val xmpService = XmpService(cli, ioUtil, reader, xmpParser, rampAlgo, writer)

  val application = Application(xmpService)
}
