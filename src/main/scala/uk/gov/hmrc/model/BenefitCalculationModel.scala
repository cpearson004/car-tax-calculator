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

package uk.gov.hmrc.model

import org.joda.time.LocalDate
import uk.gov.hmrc.model.EmployeePaymentsFrequency.EmployeePaymentsFrequency
import uk.gov.hmrc.time.TaxYearResolver



case class CarBenefitCalculationData(userContributingAmount: Option[Int] = None,
                                     listPrice: Int,
                                     carAvailableFrom: LocalDate,
                                     carAvailableTo: LocalDate,
                                     numDaysUnavailable: Option[Int] = None,
                                     totalDaysUnavailable: Int = 0,
                                     employeePayments: Option[Int] = None)

object CarBenefitCalculationData {
  def apply(data: NewBenefitCalculationData, totalDaysUnavailable: Int) = new CarBenefitCalculationData(
    userContributingAmount = data.userContributingAmount,
    listPrice = data.listPrice,
    carAvailableFrom = data.carBenefitStartDate,
    carAvailableTo = data.carBenefitStopDate,
    numDaysUnavailable = data.numDaysCarUnavailable,
    totalDaysUnavailable = totalDaysUnavailable,
    employeePayments = data.employeePayments)
}

case class FuelBenefitCalculationData(totalDaysCarUnavailable: Int,
                                      fuelBenefitStopDate: LocalDate,
                                      carBenefitStopDate: LocalDate)

object FuelBenefitCalculationData {

  def apply(data: NewBenefitCalculationData, totalDaysUnavailable: Int): Option[FuelBenefitCalculationData] =
    data.fuelBenefitStopDate.map(fuelBenefitStopDate => FuelBenefitCalculationData(
      totalDaysCarUnavailable = totalDaysUnavailable,
      fuelBenefitStopDate = fuelBenefitStopDate,
      carBenefitStopDate = data.carBenefitStopDate))
}

case class PercentageCalculationData(carRegisteredBefore98: Boolean,
                                     fuelType: String,
                                     co2Emission: Option[Int] = None,
                                     engineCapacity: Option[Int] = None)

object PercentageCalculationData {

  def apply(data: NewBenefitCalculationData) = new PercentageCalculationData(
    carRegisteredBefore98 = data.carRegisteredBefore98,
    fuelType = data.fuelType,
    co2Emission = data.co2Emission,
    engineCapacity = data.engineCapacity)
}

object EmployeePaymentsFrequency extends Enumeration {
  type EmployeePaymentsFrequency = Value

  val WEEKLY, MONTHLY = Value
}



case class NewBenefitCalculationData(carRegisteredBefore98: Boolean,
                                     fuelType: String,
                                     co2Emission: Option[Int],
                                     engineCapacity: Option[Int],
                                     userContributingAmount: Option[Int],
                                     listPrice: Int,
                                     carBenefitStartDate: LocalDate,
                                     carBenefitStopDate: LocalDate,
                                     numDaysCarUnavailable: Option[Int],
                                     npsNumDaysCarUnavailable: Option[Int],
                                     employeePayments: Option[Int],
                                     employeePaymentsFrequency: Option[EmployeePaymentsFrequency],
                                     fuelBenefitStopDate: Option[LocalDate]
                                      )

object NewBenefitCalculationData {
  val ERROR_CAR_VALUE_EMPTY = "Car CarValue must have a value set."


  def extractEmployeePaymentsFrequency(privateUsePaymentsFrequency: Option[String]): Option[EmployeePaymentsFrequency] = {
    try {
      privateUsePaymentsFrequency.map(EmployeePaymentsFrequency.withName)
    } catch {
      case e: NoSuchElementException =>
      throw new IllegalArgumentException(s"Unrecognised privateUsePaymentsFrequency: $privateUsePaymentsFrequency")
    }
  }

  def apply(car: CarDetails, endOfTaxYear: LocalDate, npsNumDaysCarUnavailable: Option[Int] = None): NewBenefitCalculationData = {
    val fuelBenefit = car.fuelBenefit

    NewBenefitCalculationData(
      carRegisteredBefore98 = isRegisteredBeforeCutoff(car.dateCarRegistered.value),
      fuelType = Option(car.fuelType).getOrElse("NA"),
      co2Emission = car.co2Emissions,
      engineCapacity = car.engineSize,
      userContributingAmount = Some(car.capitalContributionAmount),
      listPrice = car.carValue,
      carBenefitStartDate = car.dateMadeAvailable.getOrElse(TaxYearResolver.startOfCurrentTaxYear),
      carBenefitStopDate = car.dateWithdrawn.getOrElse(endOfTaxYear),
      numDaysCarUnavailable = car.daysUnavailable,
      npsNumDaysCarUnavailable = npsNumDaysCarUnavailable,
      employeePayments = car.privateUsePaymentsAmount,
      employeePaymentsFrequency = None,
      fuelBenefitStopDate = fuelBenefit.map(_.dateWithdrawn.getOrElse(car.dateWithdrawn.getOrElse(endOfTaxYear))
      )
    )
  }

  private val carRegistrationDateCutoff = LocalDate.parse("1998-01-01")

  private def isRegisteredBeforeCutoff(date: LocalDate): Boolean = date.isBefore(carRegistrationDateCutoff)
}
