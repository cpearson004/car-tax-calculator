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


case class BasicPercentage(coRange: Range, typeAPercentage: Int, typeDPercentage: Int) extends FuelCodeBasedPercentage

object BasicPercentage extends CSVProcessor {
  type RowType = BasicPercentage

  protected[yeardata] def processLine(line: String): Either[List[String], RowType] = {
    toFields(line) match {
      case AsInt(coLower) :: AsInt(coUpper) :: AsInt(typeA) :: AsInt(typeD) :: Nil if coUpper < coLower =>
        Left(List(s"Field 2 ($coUpper) should not be less than field 1 ($coLower)"))

      case AsInt(coLower) :: AsInt(coUpper) :: AsInt(typeA) :: AsInt(typeD) :: Nil => Right(BasicPercentage(coLower to coUpper, typeA, typeD))

      case l@(a :: b :: c :: d :: Nil) => Left(badIntsErrors(l))

      case l => Left(List(s"Wrong number of fields: expected 4 but got ${l.length}"))
    }
  }
}
