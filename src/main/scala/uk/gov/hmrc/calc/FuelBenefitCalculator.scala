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

import uk.gov.hmrc.model.{Valuation, AnnualConstants, FuelBenefitCalculationData}
import uk.gov.hmrc.model.yeardata.{YearDataRepo, TaxYear}
import java.lang.Math._
import uk.gov.hmrc.time.DateTimeUtils._



object FuelBenefitCalculator extends FuelBenefitCalculator

trait FuelBenefitCalculator {

  lazy val repo = YearDataRepo

  def calculateFuelBenefitValue(appropriatePercentage: Valuation, dataOption: Option[FuelBenefitCalculationData], taxYears: TaxYears): Option[Valuation] = {
    dataOption.map { data =>
      val thisYear = calculateFuelBenefitValueForYear(data, appropriatePercentage.thisYear, taxYears.thisYear)

      val forecastFuelData = data.copy(
        totalDaysCarUnavailable = 0,
        fuelBenefitStopDate = taxYears.forecastYear.end,
        carBenefitStopDate = taxYears.forecastYear.end)
      val forecastYear = calculateFuelBenefitValueForYear(forecastFuelData, appropriatePercentage.forecastYear, taxYears.forecastYear)

      Valuation(thisYear, forecastYear)
    }
  }

   def calculateFuelBenefitValueForYear(data: FuelBenefitCalculationData, appropriatePercentage: Int, taxYearState: TaxYearState): Int = {
    if (appropriatePercentage < 0) throw new IllegalArgumentException("FinalAppropriatePercentage must be greater than 0")

    val totalDaysFuelUnavailable = data.totalDaysCarUnavailable + max(0, daysBetween(data.fuelBenefitStopDate, data.carBenefitStopDate))

    val unavailabilityRatio = 1.0 - totalDaysFuelUnavailable.toDouble / taxYearState.daysInYear.toDouble

    // TODO: What should we do if the constant isn't in the repo?
    val annualValue = repo.getAnnualConstant(taxYearState.constantsYear, AnnualConstants.CAR_FUEL_BENEFIT).get
    val finalAppropriatePercentage = appropriatePercentage.toDouble / 100

    val result = (BigDecimal(annualValue) * finalAppropriatePercentage * unavailabilityRatio).setScale(0, BigDecimal.RoundingMode.DOWN)
    result.toInt
  }
}
