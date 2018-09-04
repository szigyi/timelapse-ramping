package hu.szigyi.timelapse.ramping.io

import java.io.File

import org.scalatest.{Matchers, Outcome, fixture}

class IOUtilTest extends fixture.FunSpec with Matchers {

  private val XMPextension: String = "xmp"

  override type FixtureParam = IOUtil

  override protected def withFixture(test: OneArgTest): Outcome = test(IOUtil())

  describe("IO Util tests") {
    describe("Replace Extension Test") {
      it("should return the XMP path from simple image path") { util =>
        val imagePath = new File("/Users/us/images/218/IMG_23970.CR2")
        val xmpPath = new File("/Users/us/images/218/IMG_23970.xmp")

        val result = util.replaceExtension(imagePath, XMPextension)

        result shouldEqual xmpPath
      }
      it("should return the XMP path from whitespace image path") { util =>
        val imagePath = new File("/Users/u s/images/218/IMG 23970.CR2")
        val xmpPath = new File("/Users/u s/images/218/IMG 23970.xmp")

        val result = util.replaceExtension(imagePath, XMPextension)

        result shouldEqual xmpPath
      }
      it("should return the XMP path from dotted image path") { util =>
        val imagePath = new File("/Users/us/images/2.1.8/IMG.23970.CR2")
        val xmpPath = new File("/Users/us/images/2.1.8/IMG.23970.xmp")

        val result = util.replaceExtension(imagePath, XMPextension)

        result shouldEqual xmpPath
      }
    }
    // TODO finish the test for Filter By Extension
//    describe("Filter By Extension Test") {
//      it("") { util =>
//        d.listFiles(ioUtil.filterByExtensions(supportedExtensions))
//          .filter(_.isFile)
//          .sortWith((f1, f2) => f1.getName < f2.getName)
//          .toList
//
//      }
//    }
  }

}
