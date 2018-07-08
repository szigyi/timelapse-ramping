package hu.szigyi.timelapse.ramping.service

import java.io.File

import com.typesafe.scalalogging.LazyLogging
import hu.szigyi.timelapse.ramping.cli.CLI
import hu.szigyi.timelapse.ramping.fs.{FsUtil, Reader}
import hu.szigyi.timelapse.ramping.model.{XMP, XMPSettings}
import com.drew.imaging.ImageMetadataReader
import com.drew.metadata.Metadata

class XmpService(cli: CLI, fsUtil: FsUtil, reader: Reader) extends LazyLogging {

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
    XMP(xmpFile, xmpAsString, XMPSettings(0, 0, 0))
  }
}

object XmpService {
  def apply(cli: CLI, fsUtil: FsUtil, reader: Reader): XmpService = new XmpService(cli, fsUtil, reader)
}