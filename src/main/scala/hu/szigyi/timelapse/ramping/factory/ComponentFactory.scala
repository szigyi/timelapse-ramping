package hu.szigyi.timelapse.ramping.factory

import com.typesafe.scalalogging.LazyLogging
import hu.szigyi.timelapse.ramping.algo.{Equations, ExposureBias, MirrorPrevious}
import hu.szigyi.timelapse.ramping.cli.CLI
import hu.szigyi.timelapse.ramping.io.{IOUtil, Reader}
import hu.szigyi.timelapse.ramping.service.{XmpParser, XmpService}

trait ComponentFactory extends LazyLogging with ConfigurationFactory {

  val fsUtil = IOUtil()
  val reader = Reader(imagesConfig, fsUtil)
  val cli = CLI()

  private val equations = Equations()
  private val exposureBias = ExposureBias(equations)
  val rampAlgo = MirrorPrevious(exposureBias)

  val xmpParser = XmpParser(defaultConfig)
  val xmpService = XmpService(cli, fsUtil, reader, xmpParser, rampAlgo)
}
