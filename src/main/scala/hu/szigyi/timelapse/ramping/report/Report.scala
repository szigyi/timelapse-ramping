package hu.szigyi.timelapse.ramping.report

import scala.collection.mutable.ArrayBuffer

class Report {
  val images: ArrayBuffer[ImageReport] = new ArrayBuffer[ImageReport]()
  val mirroredEVs: ArrayBuffer[BigDecimal] = new ArrayBuffer[BigDecimal]()
  val mirroredAndSqueezedEVs: ArrayBuffer[BigDecimal] = new ArrayBuffer[BigDecimal]()
  val avg20EVs: ArrayBuffer[BigDecimal] = new ArrayBuffer[BigDecimal]()
  val interpolatedEVs: ArrayBuffer[BigDecimal] = new ArrayBuffer[BigDecimal]()

}

case class ImageReport(exposureValue: BigDecimal,
                       adjustedExposureValue: BigDecimal,
                       shutterSpeed: BigDecimal,
                       aperture: BigDecimal,
                       iso: Int)
