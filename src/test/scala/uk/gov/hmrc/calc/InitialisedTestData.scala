package uk.gov.hmrc.calc

import org.joda.time.LocalDate
import uk.gov.hmrc.calc.DataInitializer
import uk.gov.hmrc.model.{EmployeePaymentsFrequency, FuelTypes, NewBenefitCalculationData}



object InitialisedTestData extends DataInitializer {

  val cy = new LocalDate().getYear
  val benefitData = new NewBenefitCalculationData(false,
    FuelTypes.DIESEL,
    Some(125),
    Some(1400),
    Some(2000),
    15000,
    new LocalDate(cy - 1, 5, 1),
    new LocalDate(cy, 4, 5),
    None,
    None,
    Some(20),
    Some(EmployeePaymentsFrequency.WEEKLY),
    Some(new LocalDate(cy, 4, 5))
  )
}
