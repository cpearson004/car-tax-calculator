package uk.gov.hmrc.model.yeardata

import uk.gov.hmrc.model.yeardata.BasicPercentage
import org.scalatest.{Matchers, WordSpecLike, OptionValues}
class BasicPercentageSpec extends WordSpecLike with Matchers with OptionValues {

  "processLine" should {
    "successfully convert a good line" in {
      val goodLine = "1, 5, 8, 9"
      BasicPercentage.processLine(goodLine) shouldBe Right(BasicPercentage((1 to 5), 8, 9))
    }

    "fail to convert a line where one element is not an Int, giving an error message containing the bad value" in {
      val badLine = "foo, 2, 3, 4"
      val result = BasicPercentage.processLine(badLine)
      result shouldBe a[Left[List[String], _]]
      result.left.get(0) should include("foo")
    }

    "fail to convert a line where two elements are not Ints, giving an error message containing the bad values and field numbers" in {
      val badLine = "foo, 2, bar, 4"
      val result = BasicPercentage.processLine(badLine)
      result shouldBe a[Left[List[String], _]]
      result.left.get(0) should include("foo")
      result.left.get(0) should include("1")
      result.left.get(1) should include("bar")
      result.left.get(1) should include("3")
    }

    "fail to convert a line where the second value is less than the first value and so don't form a good range" in {
      val badLine = "5, 2, 3, 4"
      val result = BasicPercentage.processLine(badLine)
      result shouldBe a[Left[List[String], _]]
    }

    "fail to convert a line with 3 fields, giving an error message including the bad field count" in {
      val badLine = "1,2,3"
      val result = BasicPercentage.processLine(badLine)
      result shouldBe a[Left[List[String], _]]
      result.left.get(0) should include("3")
    }

    "fail to convert a line with 5 fields, giving an error message including the bad field count" in {
      val badLine = "1,2,3,4,5"
      val result = BasicPercentage.processLine(badLine)
      result shouldBe a[Left[List[String], _]]
      result.left.get(0) should include("5")
    }

    "fail to convert a blank string, giving an error message including the bad field count" in {
      val badLine = ""
      val result = BasicPercentage.processLine(badLine)
      result shouldBe a[Left[List[String], _]]
      result.left.get(0) should include("1")
    }
  }

}
