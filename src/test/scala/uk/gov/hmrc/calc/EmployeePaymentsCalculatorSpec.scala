package uk.gov.hmrc.calc

import org.joda.time.LocalDate
import org.scalatest.{OptionValues, Matchers, WordSpecLike}
import uk.gov.hmrc.calc.DataInitializer
import uk.gov.hmrc.model._



class EmployeePaymentsCalculatorSpec extends WordSpecLike with Matchers with OptionValues {

    InitialisedTestData.initialiseAnnualTestData()
    val cy = InitialisedTestData.cy

    "return a valuation for cy and cy+1" in {


      val taxYears = TaxYearStateCalculator.getTaxYearsState
      val result = EmployeePaymentsCalculator.apportionForNewBenefit(InitialisedTestData.benefitData, taxYears)
      result.get.thisYear shouldBe 972
      result.get.forecastYear shouldBe 1046
    }

}
