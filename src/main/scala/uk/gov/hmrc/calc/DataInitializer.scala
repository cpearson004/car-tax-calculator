package uk.gov.hmrc.calc

import uk.gov.hmrc.model.yeardata.TaxYear
import uk.gov.hmrc.resource.{AnnualDataLoader, YearDataConstants}

object DataInitializer extends DataInitializer

trait DataInitializer extends AnnualDataLoader {

  def initialiseAnnualTestData() {

    val yearDataConstants = YearDataConstants(annualConstants = "test-annual-constants.csv",
      basicPercentages = "test-basic-percentage.csv",
      carBenefitPercentagePre98 = "test-car-benefit-percentage-pre98.csv",
      carBenefitPercentagePost98 = "test-car-benefit-percentage-post98.csv",
      carBenefitPercentage = "test-car-benefit-percentage-ZE.csv",
      supportedTaxYears = List(TaxYear.TaxYear2013, TaxYear.TaxYear2014, TaxYear.TaxYear2015))

    init(yearDataConstants)
  }
}