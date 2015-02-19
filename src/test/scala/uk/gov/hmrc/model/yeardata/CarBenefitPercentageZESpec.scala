package uk.gov.hmrc.model.yeardata

import org.scalatest.{OptionValues, Matchers, WordSpecLike}
import uk.gov.hmrc.model.yeardata.CarBenefitPercentageZE

class CarBenefitPercentageZESpec extends WordSpecLike with Matchers with OptionValues {
  "processLine" should {
    "successfully convert a good line" in {
      val goodLine = "0"
      val result = CarBenefitPercentageZE.processLine(goodLine)
      result shouldBe Right(0)
    }

    "fail line conversion where a field is not an int, giving an appropriate error message containing incorrect value" in {
      val badLine = "foo"
      val result = CarBenefitPercentageZE.processLine(badLine)
      result shouldBe a[Left[List[String], _]]
      result.left.get(0) should include("foo")
    }

    "fail line conversion with 2 fields when expecting 1, giving an error message with bad field count" in {
      val badLine = "0,1"
      val result = CarBenefitPercentageZE.processLine(badLine)

      result shouldBe a [Left[List[String], _]]

      val message = result.left.get(0)
      message should include(s"Wrong number of fields: expected 1 but got 2")
    }
  }
}
