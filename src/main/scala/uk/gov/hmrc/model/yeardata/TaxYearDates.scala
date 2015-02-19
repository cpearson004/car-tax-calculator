package uk.gov.hmrc.model.yeardata

import org.joda.time.LocalDate

object TaxYearDates extends TaxYearDates

trait TaxYearDates {

  //April 6, April 5
  val taxYearMonth = 4
  val taxYearStartDate = 6
  val taxYearEndDate = 5

   def startOfTaxYear(taxYear: TaxYear) = new LocalDate(taxYear.year, taxYearMonth, taxYearStartDate)

   def endOfTaxYear(taxYear: TaxYear) = new LocalDate(taxYear.year + 1, taxYearMonth, taxYearEndDate)

}

