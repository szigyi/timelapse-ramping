package hu.szigyi.timelapse.ramping.xmp

import java.io.File

import com.typesafe.scalalogging.LazyLogging
import hu.szigyi.timelapse.ramping.algo.MirrorPrevious
import hu.szigyi.timelapse.ramping.cli.CLI
import hu.szigyi.timelapse.ramping.io.{IOUtil, Reader, Writer}
import hu.szigyi.timelapse.ramping.model.{XMP, XMPSettings}

class XmpService(cli: CLI,
                 ioUtil: IOUtil,
                 reader: Reader,
                 xmpParser: XmpParser,
                 rampMirrorPrevious: MirrorPrevious,
                 writer: Writer) extends LazyLogging {

  def getXMP(imageFile: File): XMP = {
    val xmpFile = ioUtil.replaceExtension(imageFile, ".xmp")
    val xmpAsString = getOrCreate(imageFile, xmpFile)
    parseXMP(xmpFile, xmpAsString)
  }

  def ramp(standard: XMP, image: XMP): XMP = rampMirrorPrevious.ramp(standard, image)

  def flushRampedXMP(xmp: XMP): Unit = {
    val exposureTag = s"<crs:Exposure2012>${xmp.settings.exposure}</crs:Exposure2012>"
    if (xmp.settings.exposureExistsInXMP) {
      // Update
      val updatedXMP = xmp.content.replaceFirst("<crs:Exposure2012>(.*)</crs:Exposure2012>", exposureTag)
      writer.write(xmp.xmpFilePath, updatedXMP)
    } else {
      // Add
      val beginningOfCRS = "xmlns:crs.*[^>]>{1}(\\s)<"
      val addedXMP = xmp.content.replaceFirst(beginningOfCRS, exposureTag)
      writer.write(xmp.xmpFilePath, addedXMP)
    }
  }

  private def getOrCreate(imageFile: File, xmpFile: File): String = {
    if (!reader.isExists(xmpFile)) {
      createXMP(imageFile, xmpFile)
    }
    reader.readFile(xmpFile)
  }

  private def createXMP(imageFile: File, xmpFile: File): String = {
    val imageName = imageFile.getName
    val xmpName = xmpFile.getName
    logger.debug(s"Creating XMP for $imageName")
//    cli.exec(fsUtil.workingDirectoryOf(imageFile), Seq("exiftool", "-xmp", "-b", imageName))
    cli.exec(ioUtil.workingDirectoryOf(imageFile), Seq("exiftool", "-Tagsfromfile", imageName, xmpName))
  }

  private def parseXMP(xmpFile: File, xmpAsString: String): XMP = {
    //    val metadata: Metadata = ImageMetadataReader.readMetadata(file)
    val settings = parseXMPSettings(xmpAsString)
    // TODO parse necessary settings and validate them!!!!!! else throw exception
    // TODO validate it and throw exception if there is no value!
    XMP(xmpFile, xmpAsString, settings)
  }

  private def parseXMPSettings(xmpAsString: String): XMPSettings = {
    val shutterSpeed = xmpParser.getShutterSpeed(xmpAsString)
    val aperture = xmpParser.getAperture(xmpAsString)
    val (exposure, isExists) = xmpParser.getExposure(xmpAsString)
    val iso = xmpParser.getISO(xmpAsString)

    XMPSettings(iso, shutterSpeed, aperture, exposure, isExists)
  }
}

object XmpService {
  def apply(cli: CLI,
            ioUtil: IOUtil,
            reader: Reader,
            xmpParser: XmpParser,
            rampMirrorPrevious: MirrorPrevious,
            writer: Writer): XmpService = new XmpService(cli, ioUtil, reader, xmpParser, rampMirrorPrevious, writer)
}
