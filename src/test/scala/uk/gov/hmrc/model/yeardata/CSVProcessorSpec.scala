package uk.gov.hmrc.model.yeardata

import org.scalatest.{OptionValues, Matchers, WordSpecLike}
import uk.gov.hmrc.model.yeardata.CSVProcessor

class CSVProcessorSpec extends WordSpecLike with Matchers with OptionValues {
  class TestCSVProcessor[TRow](lineProcessor: String => Either[List[String], TRow]) extends CSVProcessor {
    override type RowType = TRow

    override protected def processLine(line: String): Either[List[String], RowType] = lineProcessor(line)
  }

  object TestCSVProcessor {
    def apply[TRow](lineProcessor: String => Either[List[String], TRow]) = new TestCSVProcessor(lineProcessor)
  }

  "fromCSV" should {
    "convert each input line to an output row" in {
      val testProcessor = TestCSVProcessor { s => Right(s)}

      val testCSV = List("a", "b")
      val result = testProcessor.fromCSVLines(testCSV)

      result.isRight shouldBe true

      val convertedData = result.right.get
      convertedData shouldBe testCSV
    }

    "fail to convert when one line is bad" in {
      val testProcessor = TestCSVProcessor { s => if (s == "pass") Right("") else Left(List(""))}
      val testCSV = List("pass","fail")

      val result = testProcessor.fromCSVLines(testCSV)
      result.isLeft shouldBe true
    }

    "Produce error messages with the numbers of the lines that were bad" in {
      val testProcessor = TestCSVProcessor { s => if (s == "pass") Right("") else Left(List(""))}

      val testCSV = List("fail", "fail")

      val result = testProcessor.fromCSVLines(testCSV)
      result.isLeft shouldBe true
      result.left.get(0) should include("line 1")
      result.left.get(1) should include("line 2")
    }

    "Produce error messages in the same order as the incoming lines" in {
      val testProcessor = TestCSVProcessor { s => Left(List(s))}
      val testCSV = List("foo", "bar")

      val result = testProcessor.fromCSVLines(testCSV)
      result.isLeft shouldBe true
      result.left.get(0) should include("foo")
      result.left.get(1) should include("bar")
    }
  }

}
