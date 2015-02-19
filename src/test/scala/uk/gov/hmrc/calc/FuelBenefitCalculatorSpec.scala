package uk.gov.hmrc.calc

import org.joda.time.LocalDate
import org.scalatest.{Matchers, WordSpecLike, OptionValues}
import uk.gov.hmrc.calc.DataInitializer
import uk.gov.hmrc.model._
import uk.gov.hmrc.model.yeardata.{TaxYear, TaxYearDates, AnnualConstants}



class FuelBenefitCalculatorSpec extends WordSpecLike with Matchers with OptionValues with DataInitializer{

  InitialisedTestData.initialiseAnnualTestData()
  val cy = InitialisedTestData.cy

  "return a valuation for cy and cy+1" in {


    val taxYears = TaxYearStateCalculator.getTaxYearsState
    val data = FuelBenefitCalculationData(InitialisedTestData.benefitData , 25)
    val appropriatePercentage = Valuation(21, 23)
    val result = FuelBenefitCalculator.calculateFuelBenefitValue(appropriatePercentage, data, taxYears)
    result.get.thisYear shouldBe 4244
    result.get.forecastYear shouldBe 5083
  }



}