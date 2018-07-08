package hu.szigyi.timelapse.ramping.bchm

import com.typesafe.scalalogging.LazyLogging

object Perf extends LazyLogging {

  def measure[T](any: => T): T = {
    val start = System.currentTimeMillis()
    val res = any
    val end = System.currentTimeMillis()
    logger.info(s"${any.getClass} - ${end - start} ms")
    res
  }
}
