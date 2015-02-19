package uk.gov.hmrc.model.yeardata

import org.scalatest.{OptionValues, Matchers, WordSpecLike}
import uk.gov.hmrc.model.yeardata.CarBenefitPercentagesPost98

class CarBenefitPercentagesPost98Spec extends WordSpecLike with Matchers with OptionValues {
  "processLine" should {
    "successfully convert a good line" in {
      val goodLine = "1401,2000,25,28"
      val result = CarBenefitPercentagesPost98.processLine(goodLine)
      result shouldBe Right(CarBenefitPercentagesPost98(1401 to 2000, 25, 28))
    }

    "fail line conversion where a field is not an int, giving an appropriate error message containing incorrect value" in {
      val badLine = "1401,foo,25,28"
      val result = CarBenefitPercentagesPost98.processLine(badLine)
      result shouldBe a[Left[List[String], _]]
      result.left.get(0) should include("foo")
      result.left.get(0) should include("Field 2")
    }

    "fail line conversion where multiple fields are not ints, giving an appropriate error message for both incorrect values" in {
      val badLine = "1401,foo,25,bar"
      val result = CarBenefitPercentagesPost98.processLine(badLine)

      result shouldBe a[Left[List[String], _]]

      {
        val message0 = result.left.get(0)
        message0 should include("foo")
        message0 should include("Field 2")
      }

      {
        val message1 = result.left.get(1)
        message1 should include("bar")
        message1 should include("Field 4")
      }
    }

    "fail line conversion where the first value is larger than the first value, resulting in an invalid range" in {
      val badLine = "2000,1401,25,28"
      val result = CarBenefitPercentagesPost98.processLine(badLine)

      result shouldBe a[Left[List[String], _]]

      val message = result.left.get(0)
      message should include("Field 2 (2000) should not be less than field 1 (1401)")
    }

    "fail line conversion with 3 fields when expecting 4, giving an error message with bad field count" in {
      val badLine = "1401,2000,25"
      val result = CarBenefitPercentagesPost98.processLine(badLine)

      result shouldBe a[Left[List[String], _]]

      val message = result.left.get(0)
      message should include(s"Wrong number of fields: expected 4 but got 3")
    }

    "fail line conversion with 5 fields when expecting 4, giving an error message with bad field count" in {
      val badLine = "1401,2000,25,28,30"
      val result = CarBenefitPercentagesPost98.processLine(badLine)

      result shouldBe a[Left[List[String], _]]

      val message = result.left.get(0)
      message should include(s"Wrong number of fields: expected 4 but got 5")
    }

  }
}
