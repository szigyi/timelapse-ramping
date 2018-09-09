package hu.szigyi.timelapse.ramping.xmp

import java.io.File

import com.drew.imaging.ImageMetadataReader._
import com.drew.metadata.{Directory, Metadata}
import com.drew.metadata.exif.ExifSubIFDDirectory
import com.drew.metadata.exif.makernotes.CanonMakernoteDirectory
import com.typesafe.scalalogging.LazyLogging
import hu.szigyi.timelapse.ramping.algo.ramp.{Interpolator, RampHelper}
import hu.szigyi.timelapse.ramping.cli.CLI
import hu.szigyi.timelapse.ramping.conf.{DefaultConfig}
import hu.szigyi.timelapse.ramping.io.{IOUtil, Reader, Writer}
import hu.szigyi.timelapse.ramping.model.{EXIF, EXIFSettings}

class Service(defaultConfig: DefaultConfig,
              cli: CLI,
              ioUtil: IOUtil,
              reader: Reader,
              exifParser: EXIFParser,
              rampHelper: RampHelper,
              ramp: Interpolator,
              writer: Writer) extends LazyLogging {

  def getEXIF(imageFile: File): EXIF = {
    val metadata: Metadata = readMetadata(imageFile)
    parseXMP(imageFile, metadata)
  }

  def rampExposure(exifs: Seq[EXIF]): Seq[BigDecimal] = {
    val EVs: Seq[BigDecimal] = exifs.map(exif => rampHelper.calculateEV(exif))
    implicit val f = ramp.buildEVInterpolator(EVs)
    val indicesOfEXIFs = (0 to exifs.size - 1)
    indicesOfEXIFs.map(index => ramp.interpolate(index)(f))
  }

  def rampWhiteBalance(exifs: Seq[EXIF]): Seq[Int] = {
    val f = ramp.buildWBInterpolator(exifs.map(exif => exif.settings.temperature))
    val indicesOfEXIFs = (0 to exifs.size - 1)
    val rampedWBs = indicesOfEXIFs.map(index => (ramp.interpolate(index)(f)).toInt)
    rampedWBs
  }

  def flushXMP(exif: EXIF): Unit = {
    val xmpStr = s"""
       |<x:xmpmeta xmlns:x='adobe:ns:meta/' x:xmptk='hu.szigyi.timelapse.ramping'>
       |<rdf:RDF xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#'>
       | <rdf:Description rdf:about=''
       |  xmlns:crs='http://ns.adobe.com/camera-raw-settings/1.0/'
       |  crs:Exposure2012="${exif.settings.exposure}"
       |  crs:Temperature="${exif.settings.temperature}"
       |  crs:Tint="0"
       |  >
       | </rdf:Description>
       |</rdf:RDF>
       |</x:xmpmeta>
     """.stripMargin
//    XmpWriter.write(new FileOutputStream(xmp.xmpFilePath), xmp.metadata)
    writer.write(exif.xmpFilePath, xmpStr)
//    logger.info(s"XMP is created: ${xmp.xmpFilePath}")
  }

  private def parseXMP(imageFile: File, metadata: Metadata) = {
    val exifDir = getMetadataDirectory(imageFile, metadata, classOf[ExifSubIFDDirectory])
    // TODO get the right Dir, you can use the exif tag to find out what the brand of the camera, or use ENV variable by user
    val canonDir = getMetadataDirectory(imageFile, metadata, classOf[CanonMakernoteDirectory])

    val exifSettings = parseEXIFSettings(exifDir, canonDir)

    val xmpFile = ioUtil.replaceExtension(imageFile, "xmp")
    EXIF(xmpFile, exifSettings)
  }

  private def getMetadataDirectory[T <: Directory](file: File, metadata: Metadata, clazz: Class[T]): T = {
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

  private def parseEXIFSettings(exifDir: ExifSubIFDDirectory, canonDir: CanonMakernoteDirectory): EXIFSettings = {
    val shutterSpeed = exifParser.getShutterSpeed(exifDir)
    val aperture = exifParser.getAperture(exifDir)
    val (exposure, isExists) = exifParser.getExposure(exifDir)
    val iso = exifParser.getISO(exifDir)
    val wb = exifParser.getTemperature(canonDir)

    EXIFSettings(iso, shutterSpeed, aperture, exposure, isExists, wb)
  }
}

object Service {
  def apply(defaultConfig: DefaultConfig,
            cli: CLI,
            ioUtil: IOUtil,
            reader: Reader,
            exifParser: EXIFParser,
            rampHelper: RampHelper,
            ramp: Interpolator,
            writer: Writer): Service = new Service(defaultConfig, cli, ioUtil, reader, exifParser, rampHelper, ramp, writer)
}
