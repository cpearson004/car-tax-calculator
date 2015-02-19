package uk.gov.hmrc.calc

import org.joda.time.LocalDate
import org.scalatest.{OptionValues, Matchers, WordSpecLike}
import uk.gov.hmrc.calc.{DataInitializer}
import uk.gov.hmrc.model._



class CarBenefitPercentageCalculatorSpec extends WordSpecLike with Matchers with OptionValues {

    InitialisedTestData.initialiseAnnualTestData()
    val cy = InitialisedTestData.cy

    "return a valuation for cy and cy+1" in {


      val data = PercentageCalculationData(InitialisedTestData.benefitData)
      val taxYears = TaxYearStateCalculator.getTaxYearsState
      val result = CarBenefitPercentageCalculator.calculateFinalAppropriatePercentage(data, taxYears)
      result.thisYear shouldBe 21
      result.forecastYear shouldBe 23
    }
}
