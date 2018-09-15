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

  private val ev = EV()
  private val rampHelper = RampHelper(ev)
  val interpolator = Interpolator(rampHelper)

  val exifParser = EXIFParser(defaultConfig)
  val exifService = Service(defaultConfig, ioUtil, reader, exifParser, rampHelper, interpolator, writer)

  val application = Application(exifService)
}
