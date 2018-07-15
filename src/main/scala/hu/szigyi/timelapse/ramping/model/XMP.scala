package hu.szigyi.timelapse.ramping.model

import java.io.File

case class XMP(xmpFilePath: File,
               content: String,
               settings: XMPSettings) {
  override def toString: String =
    s"""
       |XMP($settings, $xmpFilePath)""".stripMargin
}

case class XMPSettings(iso: Int,
                       shutterSpeed: BigDecimal,
                       aperture: BigDecimal,
                       exposure: BigDecimal,
                       exposureExistsInXMP: Boolean) {
  override def toString: String = s"{iso: $iso, shutterSpeed: $shutterSpeed, aperture: $aperture, exposure: $exposure}"
}
