package hu.szigyi.timelapse.ramping.service

import java.io.File

import com.typesafe.scalalogging.LazyLogging
import hu.szigyi.timelapse.ramping.cli.CLI
import hu.szigyi.timelapse.ramping.conf.Default
import hu.szigyi.timelapse.ramping.fs.{FsUtil, Reader}
import hu.szigyi.timelapse.ramping.model.{XMP, XMPSettings}
import kantan.xpath.{XPathError, XPathResult}
//import com.drew.imaging.ImageMetadataReader
//import com.drew.metadata.Metadata

class XmpService(default: Default,
                 cli: CLI,
                 fsUtil: FsUtil,
                 reader: Reader) extends LazyLogging {

  def getXMP(imageFile: File): XMP = {
    val xmpFile = fsUtil.replaceExtension(imageFile, ".xmp")
    val xmpAsString = getOrCreate(imageFile, xmpFile)
    parseXMP(xmpFile, xmpAsString)
  }

  private def getOrCreate(imageFile: File, xmpFile: File): String = {
    if (reader.isExists(xmpFile)) {
      reader.readFile(xmpFile)
    } else {
      createXMP(imageFile)
    }
  }

  private def createXMP(imageFile: File): String = {
    val name = imageFile.getName
    logger.debug(s"Creating XMP for $name")
    cli.exec(fsUtil.workingDirectoryOf(imageFile), Seq("exiftool", "-xmp", "-b", name))
  }

  private def parseXMP(xmpFile: File, xmpAsString: String): XMP = {
    //    val metadata: Metadata = ImageMetadataReader.readMetadata(file)
    val settings = parseXMPSettings(xmpAsString)
    // TODO parse necessary settings and validate them!!!!!! else throw exception
    // TODO validate it and throw exception if there is no value!
    XMP(xmpFile, xmpAsString, settings)
  }

  private def parseXMPSettings(xmpAsString: String): XMPSettings = {
    val exposure = getShutterSpeed(xmpAsString)
    val iso = getISO(xmpAsString)
    val aperture = getAperture(xmpAsString)
    val bias = getExposureBias(xmpAsString)

    XMPSettings(iso, exposure, aperture, bias)
  }

  private def getShutterSpeed(xmpAsString: String): BigDecimal = {
    import kantan.xpath.implicits._
    val shutterSpeedPath = xp"//@ExposureTime"
    val shutterSpeedStr = unliftOrError(xmpAsString.evalXPath[String](shutterSpeedPath))
    calculateFromRationalNumber(shutterSpeedStr)
  }

  private def getISO(xmpAsString: String): Int = {
    import kantan.xpath.implicits._
    val isoPath = xp"//ISOSpeedRatings/Seq/li[1]"
    val xPathResult = xmpAsString.evalXPath[Int](isoPath)
    unliftOrError(xPathResult)
  }

  private def getAperture(xmpAsString: String): BigDecimal = {
    import kantan.xpath.implicits._
    val aperturePath = xp"//@ApertureValue"

    def evalAperture: BigDecimal = {
      if (default.manualLens) {
        default.aperture
      } else {
        val apertureStr = xmpAsString.evalXPath[String](aperturePath)
        calculateFromRationalNumber(unliftOrError(apertureStr))
      }
    }
    evalAperture
  }

  private def getExposureBias(xmpAsString: String): BigDecimal = {
    import kantan.xpath.implicits._
    val biasPath = xp"//@ExposureBiasValue"
    val biasStr = unliftOrError(xmpAsString.evalXPath[String](biasPath))
    calculateFromRationalNumber(biasStr)
  }

  private def unliftOrError[T](result: XPathResult[T]): T = result match {
    case Right(r) => r
    case Left(error: XPathError) => throw error
  }

  private def calculateFromRationalNumber(str: String): BigDecimal = {
    val nomDom = str.split("/")
    val numerator = BigDecimal(nomDom(0).toInt)
    val denominator = BigDecimal(nomDom(1).toInt)
    numerator / denominator
  }
}

object XmpService {
  def apply(default: Default,
            cli: CLI,
            fsUtil: FsUtil,
            reader: Reader): XmpService = new XmpService(default, cli, fsUtil, reader)
}