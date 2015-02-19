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
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._



case class Questionable[A](value: A, displayable: Boolean)

object Questionable {
  implicit def reads[T](implicit tReader: Reads[T]): Reads[Questionable[T]] = (
    (JsPath \ "value").read[T] and
      (JsPath \ "displayable").read[Boolean]
    )(Questionable.apply[T] _)

  implicit def writes[T](implicit fmt: Writes[T]): Writes[Questionable[T]] = new Writes[Questionable[T]] {
    override def writes(o: Questionable[T]): JsValue = Json.obj(
      "displayable" -> JsBoolean(o.displayable),
      "value" -> Json.toJson(o.value)
    )
  }
}

object Fuel {
  implicit val format = Json.format[Fuel]
}

case class Fuel(dateMadeAvailable: Option[LocalDate] = None,
                dateWithdrawn: Option[LocalDate] = None,
                benefitAmount: Int,
                actions: Map[String, String] = Map.empty)


object CarDetails {
  implicit val format = Json.format[CarDetails]
}

case class CarDetails(
                       carSequenceNumber: Option[Int],
                       makeModel: Option[String] = None,
                       dateCarRegistered: Questionable[LocalDate],
                       fuelType: String,
                       co2Emissions: Option[Int] = None,
                       engineSize: Option[Int] = None,
                       mileageBand: Option[String] = None,
                       carValue: Int,
                       dateMadeAvailable: Option[LocalDate] = None,
                       dateWithdrawn: Option[LocalDate] = None,
                       capitalContributionAmount: Int,
                       privateUsePaymentsAmount: Option[Int] = None,
                       daysUnavailable: Option[Int] = None,
                       actions: Map[String, String] = Map.empty,
                       benefitAmount: Int,
                       fuelBenefit: Option[Fuel],
                       carShared: Option[Boolean] = None)


object CarBenefit {
  implicit val format = Json.format[CarBenefit]
}

//TODO change to Car after refactor
case class CarBenefit(
                       taxYear:Int,
                       grossAmount: Int,
                       costAmount: Option[Int] = None,
                       employmentSequenceNumber: Int,
                       amountMadeGood: Option[Int] = None,
                       cashEquivalent: Option[Int] = None,
                       expensesIncurred: Option[Int] = None,
                       amountOfRelief: Option[Int] = None,
                       paymentOrBenefitDescription: Option[String] = None,
                       fuelBenefitGrossAmount: Option[Int] = None,
                       carDetails: Seq[CarDetails]
                       )


object FuelTypes {
  val DIESEL = "diesel"
  val ELECTRICITY = "electricity"
  val OTHER = "other"

  sealed trait FuelCode {
    def code: Char
  }

  object FuelCodeDiesel extends FuelCode {
    val code = 'D'
  }

  object FuelCodeOther extends FuelCode {
    val code = 'A'
  }

  def getFuelTypeChar(fuelType: String) = {
    fuelType match {
      case ELECTRICITY => throw new Exception("Unsupported fuel type")
      case DIESEL => FuelCodeDiesel
      case OTHER => FuelCodeOther
    }
  }

  def toNpsValue(fuelType: String) = {
    fuelType match {
      case ELECTRICITY => NpsFuelTypes.ELECTRICITY
      case DIESEL => NpsFuelTypes.DIESEL
      case OTHER => NpsFuelTypes.OTHER
    }
  }

  def fromNpsValue(fuelType: Int) = {
    fuelType match {
      case NpsFuelTypes.ELECTRICITY => ELECTRICITY
      case NpsFuelTypes.DIESEL => DIESEL
      case _ => OTHER
    }
  }
}
object NpsFuelTypes {
  val DIESEL = 4
  val ELECTRICITY = 5
  val OTHER = 10
}

object BenefitTypes {
  val CAR_CODE = 31
  val FUEL_CODE = 29
}

object EngineSizeTypes {

  val LESS_THAN_1400 = 1400
  val LESS_THAN_2000 = 2000
  val MORE_THAN_2000 = 9999

  def toNpsValue(engineSize: Int) = {
    engineSize match {
      case size if (0 to LESS_THAN_1400).contains(size) => 1
      case size if (LESS_THAN_1400 to LESS_THAN_2000).contains(size) => 2
      case _ => 3
    }
  }

  def fromNpsValue(engineSize: Int) = {
    engineSize match {
      case 1 => LESS_THAN_1400
      case 2 => LESS_THAN_2000
      case 3 => MORE_THAN_2000
      case invalidEngine => throw new IllegalArgumentException(s"Unexpected value for Engine Size! Acceptable values are [1, 2, 3] but found invalid value of [$invalidEngine]")
    }
  }

}

