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
package uk.gov.hmrc.model.yeardata

import uk.gov.hmrc.model.FuelTypes
import uk.gov.hmrc.model.FuelTypes.FuelCode



object YearDataRepo {
  private var _repoData: Map[TaxYear, TaxYearData] = Map.empty

  lazy val yearsAvailable = _repoData.keys.toList

  def init(data: Map[TaxYear, TaxYearData]): Unit = {
    _repoData = data
  }

  private def basicPercentages(taxYear: TaxYear): Option[BasicPercentages] =
    _repoData.get(taxYear).map(_.basicPercentages)

  private def pre98(taxYear: TaxYear): Option[CarBenefitsPre98] =
    _repoData.get(taxYear).map(_.carBenefitsPre98)

  private def post98(taxYear: TaxYear): Option[CarBenefitsPost98] =
    _repoData.get(taxYear).map(_.carBenefitsPost98)

  private def ze(taxYear: TaxYear): Option[Int] =
    _repoData.get(taxYear).map(_.carBenefitPercentageZE)

  private def annualConstants(taxYear: TaxYear): Option[Map[String, Int]] =
    _repoData.get(taxYear).map(_.annualConstants)

  def getMaxAvailableCO2Emission(taxYear: TaxYear): Option[Int] = {
    basicPercentages(taxYear).map(_.maxAvailableCO2Emission)
  }

  def getBasicPercentage(taxYear: TaxYear, fuelCode: FuelCode, emission: Int): Option[Int] = {
    for {
      bps <- basicPercentages(taxYear)
      bp <- bps.findByEmission(emission)
    } yield fuelCode match {
      case FuelTypes.FuelCodeDiesel => bp.typeDPercentage
      case FuelTypes.FuelCodeOther => bp.typeAPercentage
    }
  }

  def getCarBenefitPercentagePre98(taxYear: TaxYear, engineCapacity: Int): Option[Int] = {
    for {
      pre98 <- pre98(taxYear)
      byCapacity <- pre98.findByCapacity(engineCapacity)
    } yield byCapacity.percentage
  }

  def getCarBenefitPercentagePost98(taxYear: TaxYear, fuelCode: FuelCode, engineCapacity: Int): Option[Int] = {
    for {
      post98 <- post98(taxYear)
      byCapacity <- post98.findByCapacity(engineCapacity)
    } yield fuelCode match {
      case FuelTypes.FuelCodeDiesel => byCapacity.typeDPercentage
      case FuelTypes.FuelCodeOther => byCapacity.typeAPercentage
    }
  }

  def getCarBenefitPercentageZE(taxYear: TaxYear): Option[Int] = ze(taxYear)

  def getAnnualConstant(taxYear: TaxYear, key: String): Option[Int] = annualConstants(taxYear).flatMap(_.get(key))

}