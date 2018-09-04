package hu.szigyi.timelapse.ramping.factory

import com.typesafe.scalalogging.LazyLogging
import hu.szigyi.timelapse.ramping.Application
import hu.szigyi.timelapse.ramping.algo.ev.{EV, EVDifference}
import hu.szigyi.timelapse.ramping.algo.ramp.{AverageWindow, Interpolator, RampHelper}
import hu.szigyi.timelapse.ramping.cli.CLI
import hu.szigyi.timelapse.ramping.io.{IOUtil, Reader, Writer}
import hu.szigyi.timelapse.ramping.xmp.{XmpParser, XmpService}

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

  val xmpParser = XmpParser(defaultConfig)
  val xmpService = XmpService(cli, ioUtil, reader, xmpParser, rampHelper, rampAlgo, writer)

  val application = Application(xmpService)
}
