package hu.szigyi.timelapse.ramping.service

import java.io.File

import com.drew.imaging.ImageMetadataReader._
import com.drew.metadata.{Directory, Metadata => Md}
import com.drew.metadata.exif.ExifSubIFDDirectory
import com.drew.metadata.exif.makernotes.CanonMakernoteDirectory
import com.typesafe.scalalogging.LazyLogging
import hu.szigyi.timelapse.ramping.algo.ramp.{Interpolator, RampHelper}
import hu.szigyi.timelapse.ramping.conf.DefaultConfig
import hu.szigyi.timelapse.ramping.io.{IOUtil, Reader, Writer}
import hu.szigyi.timelapse.ramping.model.{Metadata, Processed}
import hu.szigyi.timelapse.ramping.parse.EXIFParser
import hu.szigyi.timelapse.ramping.validate.EXIFValidator
import hu.szigyi.timelapse.ramping.validate.EXIFValidator.EXIFValid

import scala.util.Failure

class Service(defaultConfig: DefaultConfig,
              ioUtil: IOUtil,
              reader: Reader,
              exifParser: EXIFParser,
              exifValidator: EXIFValidator,
              rampHelper: RampHelper,
              ramp: Interpolator,
              writer: Writer) extends LazyLogging {

  def getEXIF(imageFile: File): EXIFValid[Metadata] = {
    val imageMetadata: Md = readMetadata(imageFile)
    val imageExifDir = getMetadataDirectory(imageFile, imageMetadata, classOf[ExifSubIFDDirectory])
    // TODO get the right Dir, you can use the exif tag to find out what the brand of the camera, or use ENV variable by user
    val canonDir = getMetadataDirectory(imageFile, imageMetadata, classOf[CanonMakernoteDirectory])

    val shutterSpeed = exifParser.getShutterSpeed(imageExifDir)
    val aperture = exifParser.getAperture(imageExifDir)
    val originalExposure = exifParser.getExposure(imageExifDir)
    val iso = exifParser.getISO(imageExifDir)
    val temperature = exifParser.getTemperature(canonDir)

    val xmpFile = ioUtil.replaceExtension(imageFile, "xmp")

    // TODO get exposure from xmp file if there is any and use that one instead of the image's exposure
//    val xmpMetadata = readMetadata(xmpFile)
//    val xmpExifDir = getMetadataDirectory(xmpFile, xmpMetadata, classOf[ExifSubIFDDirectory])
//    val xmpExposure = exifParser.getExposure(xmpExifDir)
    val xmpExposure = Failure(new RuntimeException)

    val exposure = xmpExposure.getOrElse(originalExposure)

    exifValidator.validateEXIF(imageFile.toPath.toAbsolutePath.toString, xmpFile, iso, shutterSpeed, aperture, exposure, temperature)
  }

  def rampExposure(exifs: Seq[Metadata]): Seq[BigDecimal] = {
    val EVs: Seq[BigDecimal] = exifs.map(exif => rampHelper.calculateEV(exif))
    implicit val f = ramp.buildEVInterpolator(EVs)
    val indicesOfEXIFs = (0 to exifs.size - 1)
    indicesOfEXIFs.map(index => ramp.interpolate(index)(f))
  }

  def rampTemperature(exifs: Seq[Metadata]): Seq[Int] = {
    val f = ramp.buildTemperatureInterpolator(exifs.map(exif => exif.settings.temperature))
    val indicesOfEXIFs = (0 to exifs.size - 1)
    val rampedTemps = indicesOfEXIFs.map(index => (ramp.interpolate(index)(f)).toInt)
    rampedTemps
  }

  def flushXMP(exif: Processed): Unit = {
    val xmpStr = s"""
       |<x:xmpmeta xmlns:x='adobe:ns:meta/' x:xmptk='hu.szigyi.timelapse.ramping'>
       |<rdf:RDF xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#'>
       | <rdf:Description rdf:about=''
       |  xmlns:crs='http://ns.adobe.com/camera-raw-settings/1.0/'
       |  crs:Exposure2012="${exif.processedSettings.exposure.getOrElse(exif.metadata.settings.exposure)}"
       |  crs:Temperature="${exif.processedSettings.temperature.getOrElse(exif.metadata.settings.temperature)}"
       |  crs:Tint="0"
       |  >
       | </rdf:Description>
       |</rdf:RDF>
       |</x:xmpmeta>
     """.stripMargin
//    XmpWriter.write(new FileOutputStream(xmp.xmpFilePath), xmp.metadata)
    writer.write(exif.metadata.xmpFilePath, xmpStr)
//    logger.info(s"XMP is created: ${xmp.xmpFilePath}")
  }

  def flushReport(reportFile: File, csv: String): Unit = writer.write(reportFile, csv)

  private def getMetadataDirectory[T <: Directory](file: File, metadata: Md, clazz: Class[T]): T = {
    val dir = metadata.getFirstDirectoryOfType(clazz)

    if (null == dir) {
      throw new RuntimeException(
        s"""
           |FATAL ERROR
           |There is no EXIF information in the given picture!
           |${file.getAbsolutePath}
           |
           |Cannot proceed because to set any metadata it requires to have metadata (EXIF) in the picture.
           """.stripMargin)
    } else {
      dir
    }
  }
}

object Service {
  def apply(defaultConfig: DefaultConfig,
            ioUtil: IOUtil,
            reader: Reader,
            exifParser: EXIFParser,
            exifValidator: EXIFValidator,
            rampHelper: RampHelper,
            ramp: Interpolator,
            writer: Writer): Service = new Service(defaultConfig, ioUtil, reader, exifParser, exifValidator, rampHelper, ramp, writer)
}
