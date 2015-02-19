/*
 * Copyright 2014 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.gov.hmrc.calc

import uk.gov.hmrc.model.yeardata.TaxYear
import org.joda.time.LocalDate
import uk.gov.hmrc.time.DateTimeUtils._
import uk.gov.hmrc.model.yeardata.TaxYearDates



object TaxYearUnavailabilityCalculator extends TaxYearUnavailabilityCalculator

trait TaxYearUnavailabilityCalculator {

  def totalDays(taxYear: TaxYear, carAvailableFrom: LocalDate, carAvailableTo: LocalDate, numDaysUnavailable: Option[Int]) = {
    startDaysUnavailable(taxYear, carAvailableFrom) + endDaysUnavailable(taxYear, carAvailableTo) + numDaysUnavailable.getOrElse(0)
  }

  private def startDaysUnavailable(taxYear: TaxYear, carAvailableFrom: LocalDate): Int = {
    if (TaxYearDates.startOfTaxYear(taxYear).isAfter(carAvailableFrom)) 0 else daysBetween(TaxYearDates.startOfTaxYear(taxYear), carAvailableFrom)
  }

  private def endDaysUnavailable(taxYear: TaxYear, carAvailableTo: LocalDate): Int = {
    if (carAvailableTo.isBefore(TaxYearDates.endOfTaxYear(taxYear))) daysBetween(carAvailableTo, TaxYearDates.endOfTaxYear(taxYear)) else 0
  }
}
