package hu.szigyi.timelapse.ramping.algo.ev

import ch.obermuhlner.math.big.BigDecimalMath._
import hu.szigyi.timelapse.ramping.math.BigDecimalConverter._
import hu.szigyi.timelapse.ramping.math.BigDecimalDecorator._
import hu.szigyi.timelapse.ramping.model.EXIF

import scala.math.BigDecimal._
import scala.math._

/**
  * Equations are based on the wikipedia page of Exposure Value https://en.wikipedia.org/wiki/Exposure_value<br/>
  * <br/>
  * EV equation<br/>
  * EV=log2(N^2^/t)<br/>
  * N = relative aperture<br/>
  * t = shutter speed<br/>
  */
class EV {

  /**
    * EV=log2(N^2^/t)<br/>
    * N = relative aperture<br/>
    * t = shutter speed<br/>
    * <br/>
    * If ISO is not 100, then<br/>
    * EV+=log2(S/100)<br/>
    * S = ISO<br/>
    * @param shutterSpeed
    * @param aperture
    * @param iso
    * @return
    */
  def EV(aperture: BigDecimal, shutterSpeed: BigDecimal, iso: Int): BigDecimal = {
    val div = aperture.`^2` / shutterSpeed
    val EVCompensation = log2(div, defaultMathContext)

    if (iso.equals(100)) EVCompensation
    else EVCompensation + log2(BigDecimal(iso) / 100, defaultMathContext)
  }

  /**
    * EVdiff=log2(base/current)<br/>
    * @param base base shutter speed
    * @param current current shutter speed
    * @return EVdiff which is positive if current should be brighter or negative if current should be darker
    */
  def shutterSpeeds(base: BigDecimal, current: BigDecimal): BigDecimal = {
    val div = base / current
    log2(div, defaultMathContext)
  }

  /**
    * EVdiff=log2(current^2^/base^2^)<br/>
    * @param base base aperture
    * @param current current aperture
    * @return EVdiff which is positive if current should be brighter or negative if current should be darker
    */
  def apertures(base: BigDecimal, current: BigDecimal): BigDecimal = {
    val div = current.`^2` / base.`^2`
    log2(div, defaultMathContext)
  }

  /**
    * EVdiff=log2(base/current)<br/>
    * @param base base ISO
    * @param current current ISO
    * @return EVdiff which is positive if current should be brighter or negative if current should be darker
    */
  def ISOs(base: Int, current: Int): BigDecimal = {
    val div = BigDecimal(base) / current
    log2(div, defaultMathContext)
  }
}
object EV {
  def apply(): EV = new EV()
}