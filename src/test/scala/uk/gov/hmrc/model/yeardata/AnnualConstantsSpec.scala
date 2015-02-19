package uk.gov.hmrc.model.yeardata

import org.scalatest.{Matchers, WordSpecLike, OptionValues}
import uk.gov.hmrc.model.yeardata.AnnualConstants


class AnnualConstantsSpec extends WordSpecLike with Matchers with OptionValues  {

  val line1 = "maximumCapitalContribution, 5000"
  val testCSV = List(line1, "carFuelBenefit,21100")

  "processLine" should {
    "successfully convert a good line" in {
      AnnualConstants.processLine(line1) shouldBe Right("maximumCapitalContribution" -> 5000)
    }

    "fail to convert a line where the value element is not an Int, giving an error message containing the bad value" in {
      val badLine = "foo, bar"
      val result = AnnualConstants.processLine(badLine)
      result shouldBe a[Left[List[String], _]]
      result.left.get(0) should include("bar")
    }

    "fail to convert a line with 1 field, giving an error message including the bad field count" in {
      val badLine = "foo"
      val result = AnnualConstants.processLine(badLine)
      result shouldBe a[Left[List[String], _]]
      result.left.get(0) should include("1")
    }

    "fail to convert a line with 3 fields, giving an error message including the bad field count" in {
      val badLine = "foo,bar, baz"
      val result = AnnualConstants.processLine(badLine)
      result shouldBe a[Left[List[String], _]]
      result.left.get(0) should include("3")
    }

    "fail to convert a blank string, giving an error message including the bad field count" in {
      val badLine = ""
      val result = AnnualConstants.processLine(badLine)
      result shouldBe a[Left[List[String], _]]
      result.left.get(0) should include("1")
    }
  }

}
