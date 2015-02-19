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

import uk.gov.hmrc.model.{Valuation, AnnualConstants, CarBenefitCalculationData}
import uk.gov.hmrc.model.yeardata.YearDataRepo



object CarBenefitCalculator extends CarBenefitCalculator

trait CarBenefitCalculator {

  lazy val repo = YearDataRepo

  def calculateCarBenefitValue(data: CarBenefitCalculationData, appropriatePercentage: Valuation, employeePayments: Option[Valuation], taxYears: TaxYears): Valuation = {

    val thisYearCalculationData = data.copy(employeePayments = employeePayments.map(_.thisYear))
    val thisYear = calculateCarBenefitValueForYear(thisYearCalculationData, appropriatePercentage.thisYear, taxYears.thisYear)

    val forecastCarCalculationData = data.copy(
      carAvailableFrom = taxYears.forecastYear.start,
      carAvailableTo = taxYears.forecastYear.end,
      totalDaysUnavailable = 0,
      employeePayments = employeePayments.map(_.forecastYear))
    val forecastYear = calculateCarBenefitValueForYear(forecastCarCalculationData, appropriatePercentage.forecastYear, taxYears.forecastYear)

    Valuation(thisYear, forecastYear)
  }

   def calculateCarBenefitValueForYear(data: CarBenefitCalculationData, appropriatePercentage: Int, taxYearState: TaxYearState): Int = {
    if (appropriatePercentage < 0) throw new IllegalArgumentException("FinalAppropriatePercentage must be greater than 0")

    val totalPriceOfCar = data.listPrice - userContributingAmount(data.userContributingAmount, taxYearState)
    val preReduction = BigDecimal(totalPriceOfCar * appropriatePercentage.toDouble / 100).setScale(0, BigDecimal.RoundingMode.DOWN)
    val adjustmentForUnavailability = ((BigDecimal(data.totalDaysUnavailable) / BigDecimal(taxYearState.daysInYear)) * preReduction).setScale(0, BigDecimal.RoundingMode.UP).toInt
    val carBenefit = (preReduction.toInt - adjustmentForUnavailability) - data.employeePayments.getOrElse(0)
    Math.max(0, carBenefit)
  }

  private def userContributingAmount(userContributingAmountInput: Option[Int], taxYearState: TaxYearState) :Int = {
    val userContributingAmount = userContributingAmountInput.getOrElse(0)

    repo.getAnnualConstant(taxYearState.constantsYear, AnnualConstants.MAXIMUM_CAPITAL_CONTRIBUTION).fold(userContributingAmount)(maximumCapitalContribution =>
      Math.min(userContributingAmount, maximumCapitalContribution))
  }
}
