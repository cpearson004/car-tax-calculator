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

import uk.gov.hmrc.model.EmployeePaymentsFrequency
import uk.gov.hmrc.model.EmployeePaymentsFrequency.EmployeePaymentsFrequency
import uk.gov.hmrc.model.{Valuation, NewBenefitCalculationData}
import uk.gov.hmrc.time.DateTimeUtils._
import org.joda.time.LocalDate



object EmployeePaymentsCalculator extends EmployeePaymentsCalculator

trait EmployeePaymentsCalculator {

  def apportionForBenefitWithdrawal(data: NewBenefitCalculationData, taxYears: TaxYears): Option[Valuation] = {
    apportionForBenefitWithdrawal(data.employeePayments, data.carBenefitStartDate, data.carBenefitStopDate, data.numDaysCarUnavailable, data.npsNumDaysCarUnavailable, taxYears.thisYear)
  }

  def apportionForNewBenefit(data: NewBenefitCalculationData, taxYears: TaxYears): Option[Valuation] = {
    apportionForNewBenefit(data.employeePayments, data.employeePaymentsFrequency, data.carBenefitStartDate, data.numDaysCarUnavailable, taxYears)
  }

  private def apportionForBenefitWithdrawal(employeePayments: Option[Int], carBenefitStartDate: LocalDate, carBenefitStopDate: LocalDate, userDaysUnavailable: Option[Int], npsDaysUnavailable: Option[Int], thisYear: TaxYearState): Option[Valuation] = {
    employeePayments.map {
      employeePayments =>
        val existingApportionedDays = daysBetween(carBenefitStartDate, thisYear.end) + 1 - npsDaysUnavailable.getOrElse(0)
        val fullAnnual = BigDecimal(employeePayments) / (BigDecimal(existingApportionedDays) / BigDecimal(thisYear.daysInYear))

        val actualDaysCarAvailable = daysBetween(carBenefitStartDate, carBenefitStopDate) + 1 - userDaysUnavailable.getOrElse(0)
        val apportionedResult = fullAnnual * (BigDecimal(actualDaysCarAvailable) / BigDecimal(thisYear.daysInYear))

        Valuation(toRoundedUpInt(apportionedResult), 0)
    }
  }

  private def apportionForNewBenefit(employeePayments: Option[Int], employeePaymentsFrequency: Option[EmployeePaymentsFrequency], carBenefitStartDate: LocalDate, daysUnavailable: Option[Int], taxYears: TaxYears): Option[Valuation] = {
    employeePayments.map {
      employeePayments =>
        val fullAnnualThisYear: BigDecimal = calculateFullAnnualForNewBenefit(employeePayments, employeePaymentsFrequency, taxYears.thisYear.daysInYear)
        val actualDaysCarAvailable = daysBetween(carBenefitStartDate, taxYears.thisYear.end) + 1 - daysUnavailable.getOrElse(0)
        val apportionedResult = fullAnnualThisYear * BigDecimal(actualDaysCarAvailable) / BigDecimal(taxYears.thisYear.daysInYear)

        val fullAnnualForecastYear: BigDecimal = calculateFullAnnualForNewBenefit(employeePayments, employeePaymentsFrequency, taxYears.forecastYear.daysInYear)

        Valuation(toRoundedUpInt(apportionedResult), toRoundedUpInt(fullAnnualForecastYear))
    }
  }

  private def calculateFullAnnualForNewBenefit(employeePayments: Int, employeePaymentsFrequency: Option[EmployeePaymentsFrequency], daysInTaxYear: Int): BigDecimal = {
    import EmployeePaymentsFrequency._

    employeePaymentsFrequency match {
      case Some(MONTHLY) => BigDecimal(employeePayments) * 12
      case Some(WEEKLY) => BigDecimal(employeePayments) * (BigDecimal(daysInTaxYear) / BigDecimal(7))
      case _ => throw new IllegalArgumentException("Payment frequency not set for employeePayment")
    }
  }

  private def toRoundedUpInt(value: BigDecimal): Int = {
    value.setScale(0, BigDecimal.RoundingMode.UP).toInt
  }
}
