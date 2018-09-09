package hu.szigyi.timelapse.ramping.factory

import com.typesafe.scalalogging.LazyLogging
import hu.szigyi.timelapse.ramping.Application
import hu.szigyi.timelapse.ramping.algo.ev.{EV, EVDifference}
import hu.szigyi.timelapse.ramping.algo.ramp.{AverageWindow, Interpolator, RampHelper}
import hu.szigyi.timelapse.ramping.cli.CLI
import hu.szigyi.timelapse.ramping.io.{IOUtil, Reader, Writer}
import hu.szigyi.timelapse.ramping.xmp.{EXIFParser, Service}

trait ComponentFactory extends LazyLogging with ConfigurationFactory {

  val ioUtil = IOUtil()
  val reader = Reader(ioUtil)
  val writer = Writer()
  val cli = CLI()

  private val ev = EV()
  private val evDiff = EVDifference(ev)
  private val rampHelper = RampHelper(ev)
//  val rampAlgo = MirrorPrevious(evDiff)
//  val rampAlgo = MirrorAndSqueeze(evDiff)
//  val rampAlgo = AverageWindow(ev)
  val rampAlgo = Interpolator(rampHelper)

  val exifParser = EXIFParser(defaultConfig)
  val exifService = Service(defaultConfig, cli, ioUtil, reader, exifParser, rampHelper, rampAlgo, writer)

  val application = Application(exifService)
}
