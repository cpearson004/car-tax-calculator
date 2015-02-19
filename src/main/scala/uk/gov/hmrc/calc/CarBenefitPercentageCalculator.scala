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

import uk.gov.hmrc.model.{Valuation, PercentageCalculationData}
import uk.gov.hmrc.model.yeardata.{YearDataRepo, TaxYear}
import uk.gov.hmrc.model.FuelTypes._



object CarBenefitPercentageCalculator extends CarBenefitPercentageCalculator

trait CarBenefitPercentageCalculator {

  lazy val repo = YearDataRepo

  def calculateFinalAppropriatePercentage(data: PercentageCalculationData, taxYears: TaxYears): Valuation = {
    Valuation(
      thisYear = calculateFinalAppropriatePercentage(data, taxYears.thisYear.constantsYear),
      forecastYear = calculateFinalAppropriatePercentage(data, taxYears.forecastYear.constantsYear)
    )
  }

  def calculateFinalAppropriatePercentage(data: PercentageCalculationData, taxYear: TaxYear): Int = {
    val result = data match {
      case PercentageCalculationData(true, _, _, engineCapacity) =>
        repo.getCarBenefitPercentagePre98(taxYear, engineCapacity.getOrElse(throw new IllegalArgumentException("Engine capacity is required")))
      case PercentageCalculationData(false, ELECTRICITY, _, _) =>
        repo.getCarBenefitPercentageZE(taxYear)
      case PercentageCalculationData(false, fuelType, Some(co2Emission), _) if fuelType != ELECTRICITY =>
        repo.getBasicPercentage(taxYear, getFuelTypeChar(fuelType), Math.min(maxCO2Emissions(taxYear).get, co2Emission))
      case PercentageCalculationData(false, fuelType, None, engineCapacity) if fuelType != ELECTRICITY =>
        repo.getCarBenefitPercentagePost98(taxYear, getFuelTypeChar(fuelType), engineCapacity.getOrElse(throw new IllegalArgumentException("Engine capacity is required")))
    }
    result.getOrElse(throw new IllegalArgumentException(s"Lookup final percentage failed for $data in ${taxYear.year}"))
  }

  private def maxCO2Emissions(taxYear: TaxYear) = repo.getMaxAvailableCO2Emission(taxYear)
}
