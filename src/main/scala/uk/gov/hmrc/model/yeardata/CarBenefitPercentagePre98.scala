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


case class CarBenefitPercentagePre98(capacity: Range, percentage: Int)

object CarBenefitPercentagePre98 extends CSVProcessor {
  override type RowType = CarBenefitPercentagePre98

  override protected[yeardata] def processLine(line: String): Either[List[String], RowType] =
    toFields(line) match {
      case AsInt(engineFrom) :: AsInt(engineTo) :: AsInt(percentage) :: Nil if (engineFrom < engineTo) =>
        Right(CarBenefitPercentagePre98(engineFrom to engineTo, percentage))

      case AsInt(engineFrom) :: AsInt(engineTo) :: AsInt(percentage) :: Nil =>
        Left(List(s"Field 2 ($engineFrom) should not be less than field 1 ($engineTo)"))

      case l@(from :: to :: percentage :: Nil) => Left(badIntsErrors(l))

      case l => Left(List(s"Wrong number of fields: expected 3 but got ${l.length}"))
    }
}
