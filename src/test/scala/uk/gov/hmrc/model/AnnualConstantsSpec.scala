package uk.gov.hmrc.model

import org.scalatest.{OptionValues, Matchers, WordSpecLike}
import uk.gov.hmrc.model.AnnualConstants


class AnnualConstantsSpec extends WordSpecLike with Matchers with OptionValues {

  val line1 = "maximumCapitalContribution, 5000"
  val testCSV = List(line1, "carFuelBenefit,21100")

  "fromCSV" should {
    "convert test data to a map" in {
      val result = AnnualConstants.fromCSVLines(testCSV)

      result.isRight shouldBe true

      val convertedData = result.right.get
      convertedData.size shouldBe 2
      convertedData("maximumCapitalContribution") shouldBe 5000
      convertedData("carFuelBenefit") shouldBe 21100
    }

    "fail to convert when one line is bad" in {
      val testCSV = List(line1, "foo,bar")

      val result = AnnualConstants.fromCSVLines(testCSV)
      result.isLeft shouldBe true
    }

    "Produce error messages with the numbers of the lines that were bad" in {
      val testCSV = List("bar,foo", "foo,bar")

      val result = AnnualConstants.fromCSVLines(testCSV)
      result.isLeft shouldBe true
      result.left.get(0) should include("line 1")
      result.left.get(1) should include("line 2")
    }

    "Produce error messages in the same order as the incoming lines" in {
      val testCSV = List("bar,foo", "foo,bar")

      val result = AnnualConstants.fromCSVLines(testCSV)
      result.isLeft shouldBe true
      result.left.get(0) should include("foo")
      result.left.get(1) should include("bar")
    }
  }

  "processLine" should {
    "successfully convert a good line" in {
      AnnualConstants.processLine(line1) shouldBe Right("maximumCapitalContribution" -> 5000)
    }

    "fail to convert a line where the value element is not an Int, giving an error message containing the bad value" in {
      val badLine = "foo, bar"
      val result = AnnualConstants.processLine(badLine)
      result shouldBe a[Left[String, _]]
      result.left.get should include("bar")
    }

    "fail to convert a line with 1 field, giving an error message including the bad field count" in {
      val badLine = "foo"
      val result = AnnualConstants.processLine(badLine)
      result shouldBe a[Left[String, _]]
      result.left.get should include("1")
    }

    "fail to convert a line with 3 fields, giving an error message including the bad field count" in {
      val badLine = "foo,bar, baz"
      val result = AnnualConstants.processLine(badLine)
      result shouldBe a[Left[String, _]]
      result.left.get should include("3")
    }

    "fail to convert a blank string, giving an error message including the bad field count" in {
      val badLine = ""
      val result = AnnualConstants.processLine(badLine)
      result shouldBe a[Left[String, _]]
      result.left.get should include("1")
    }
  }

}


