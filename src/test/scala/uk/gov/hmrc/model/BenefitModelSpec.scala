package uk.gov.hmrc.model

import org.joda.time.{DateTime, LocalDate}
import play.api.libs.json._
import org.scalatest.{OptionValues, Matchers, WordSpecLike}


class BenefitModelSpec  extends WordSpecLike with Matchers with OptionValues {


  "A CarBenefit json" should {
    "deserialise correctly to a valid CarBenefit Object" in {
      val actualJson = """{
                         |"taxYear": 2014,
                         |"grossAmount": 2016,
                         |"costAmount": 2000,
                         |"employmentSequenceNumber": 16,
                         |"amountMadeGood": 18,
                         |"cashEquivalent": 100,
                         |"expensesIncurred": 23,
                         |"amountOfRelief": 34,
                         |"paymentOrBenefitDescription": "car tax",
                         |"fuelBenefitGrossAmount": null,
                         |"carDetails": []
                         |}""".stripMargin


      val car = Json.parse(actualJson).as[CarBenefit]

      car.grossAmount shouldBe (2016)
    }

    "A Questionable json" should {
      "deserialise correctly to a valid Questionable Object" in {
        val actualJson = """{
                           |"value": "2015-01-08",
                           |"displayable": false
                           |}""".stripMargin


        val date = Json.parse(actualJson).as[Questionable[LocalDate]]

        date.value.getDayOfMonth shouldBe (8)
        date.displayable shouldBe false


      }
    }

    "A Questionable object" should {
      "serialise correctly to valid Questionable json" in {

        val dt = new LocalDate().withDayOfMonth(15).withYear(2014).withMonthOfYear(1)
        val qc: Questionable[LocalDate] = Questionable(dt, false)
        val js: JsValue = Json.toJson(qc)
        js.\("displayable").toString() shouldBe "false"
        js.\("value").toString() shouldBe "\"2014-01-15\""


      }
    }

   }

}