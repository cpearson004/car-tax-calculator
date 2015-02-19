package uk.gov.hmrc.calc

import org.joda.time.LocalDate
import org.scalatest.{OptionValues, Matchers, WordSpecLike}
import uk.gov.hmrc.calc.{DataInitializer}
import uk.gov.hmrc.model._



class CarBenefitCalculatorSpec extends WordSpecLike with Matchers with OptionValues{

    InitialisedTestData.initialiseAnnualTestData()
    val cy = InitialisedTestData.cy

    "return a valuation for cy and cy+1" in {


      val taxYears = TaxYearStateCalculator.getTaxYearsState
      val data = CarBenefitCalculationData(InitialisedTestData.benefitData , 25)
      val appropriatePercentage = Valuation(21, 23)
      val employeePayments = Option(Valuation(972,1046))
      val result = CarBenefitCalculator.calculateCarBenefitValue(data,appropriatePercentage, employeePayments, taxYears)
      result.thisYear shouldBe 1571
      result.forecastYear shouldBe 1944
    }

  }
