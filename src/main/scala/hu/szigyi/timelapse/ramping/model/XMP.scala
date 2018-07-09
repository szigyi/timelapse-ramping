package hu.szigyi.timelapse.ramping.model

import java.io.File

case class XMP(filePath: File,
               content: String,
               settings: XMPSettings) {
  override def toString: String =
    s"""
       |XMP($settings, $filePath)""".stripMargin
}

case class XMPSettings(iso: Int,
                       shutterSpeed: BigDecimal,
                       aperture: BigDecimal,
                       exposureBias: BigDecimal) {
  override def toString: String = s"{iso: $iso, shutterSpeed: $shutterSpeed, aperture: $aperture, bias: $exposureBias}"
}
