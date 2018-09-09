package hu.szigyi.timelapse.ramping.algo.ramp

import org.scalatest.{FunSpec, Matchers}

class RampSpec extends FunSpec with Matchers {

  describe("Ramping should consider the already modified pics's EV as well.") {
    it("should return the EV if it is already in the file and 'larger' then the calculated value!") {
      val EV = 3.6
      val rampedEV = 2.8

//      assert(result == EV)
    }

    it("should return the ramped EV if it is 'larger' then the already modified EV") {

    }

    it("should not return the ramped EV if config says using the already modifed EV is the stanradr") {

    }
  }

}
