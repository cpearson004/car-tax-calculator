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


case class TaxYear(year: Int)

object TaxYear {
  val TaxYear2013 = TaxYear(2013)
  val TaxYear2014 = TaxYear(2014)
  val TaxYear2015 = TaxYear(2015)
}


trait ContinuousRangeCheck[T] {
  def data: List[T]

  protected def range(d: T): Range

  def rangeMax = data.map(range(_).last).max

  def rangeMin = data.map(range(_).head).min

  def expectedRange: Range = rangeMin to rangeMax

  def contains(entry: Int): Boolean = data.exists(range(_).contains(entry))

  def formsContinuousRange: Boolean = expectedRange.map(contains).contains(false)

  def rangeError: String

  require(!data.isEmpty, "Data list must have at least one entry")

  require(!formsContinuousRange, rangeError)
}

case class BasicPercentages(data: List[BasicPercentage]) extends ContinuousRangeCheck[BasicPercentage] {
  override protected def range(o: BasicPercentage) = o.coRange

  override def rangeError = "BasicPercentage entries do not form a continuous range of co2 values"

  def maxAvailableCO2Emission: Int = rangeMax

  def findByEmission(emission: Int): Option[BasicPercentage] = {
    data.find(bp => bp.coRange.contains(emission))
  }
}

case class CarBenefitsPre98(data: List[CarBenefitPercentagePre98]) extends ContinuousRangeCheck[CarBenefitPercentagePre98] {
  override protected def range(o: CarBenefitPercentagePre98) = o.capacity

  override def rangeError = "CarBenefitPercentagePre98 entries do not form a continuous range of engine capacity values"

  def findByCapacity(engineCapacity: Int): Option[CarBenefitPercentagePre98] =
    data.find(_.capacity.contains(engineCapacity))
}

case class CarBenefitsPost98(data: List[CarBenefitPercentagesPost98]) extends ContinuousRangeCheck[CarBenefitPercentagesPost98] {
  override protected def range(o: CarBenefitPercentagesPost98) = o.capacity

  override def rangeError = "CarBenefitPercentagePost98 entries do not form a continuous range of engine capacity values"

  def findByCapacity(engineCapacity: Int): Option[CarBenefitPercentagesPost98] =
    data.find(_.capacity.contains(engineCapacity))
}

case class TaxYearData(annualConstants: Map[String, Int] = Map.empty,
                       basicPercentages: BasicPercentages = BasicPercentages(List()),
                       carBenefitsPre98: CarBenefitsPre98 = CarBenefitsPre98(List()),
                       carBenefitsPost98: CarBenefitsPost98 = CarBenefitsPost98(List()),
                       carBenefitPercentageZE: Int = 0)

