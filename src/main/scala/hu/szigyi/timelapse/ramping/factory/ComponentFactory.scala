package hu.szigyi.timelapse.ramping.factory

import com.typesafe.scalalogging.LazyLogging
import hu.szigyi.timelapse.ramping.Application
import hu.szigyi.timelapse.ramping.algo.ev.EV
import hu.szigyi.timelapse.ramping.algo.ramp.{Interpolator, RampHelper}
import hu.szigyi.timelapse.ramping.io.{IOUtil, Reader, Writer}
import hu.szigyi.timelapse.ramping.parser.EXIFParser
import hu.szigyi.timelapse.ramping.report.Reporter
import hu.szigyi.timelapse.ramping.service.Service
import hu.szigyi.timelapse.ramping.validator.EXIFValidator

trait ComponentFactory extends LazyLogging with ConfigurationFactory {

  val ioUtil = IOUtil()
  val reader = Reader(ioUtil)
  val writer = Writer()

  private val ev = EV()
  private val rampHelper = RampHelper(ev)
  val interpolator = Interpolator(rampHelper)

  val validator = EXIFValidator(defaultConfig)
  val exifParser = EXIFParser(defaultConfig)
  val reporter = Reporter()
  val exifService = Service(defaultConfig, ioUtil, reader, exifParser, validator, rampHelper, interpolator, writer)

  val application = Application(exifService)
}
