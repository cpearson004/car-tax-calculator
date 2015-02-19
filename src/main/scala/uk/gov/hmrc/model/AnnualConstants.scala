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

import scala.util.Try


case class AnnualConstants(data: Map[String, Int])

object AnnualConstants {

  val MAXIMUM_CAPITAL_CONTRIBUTION = "maximumCapitalContribution"
  val CAR_FUEL_BENEFIT = "carFuelBenefit"

  object AsInt {
    def unapply(s: String): Option[Int] = Try {
      s.trim.toInt
    }.toOption
  }

  type CollectionType = Map[String, Int]
  type RowType = (String, Int)

  def emptyCollection: CollectionType = Map()

  type ErrorsOrRows = Either[List[String], CollectionType]


  def fromCSVLines(lines: List[String]): ErrorsOrRows = {
    val parsedLines = lines.map(processLine).zipWithIndex

    def errWithLineNumber(lineNumber: Int, err: String) = s"line $lineNumber: $err"

    parsedLines.foldRight[ErrorsOrRows](Right(emptyCollection)) { (elem, acc) =>
      (acc, elem) match {
        case (Right(rows), (Right(row), _)) => Right(rows + row)
        case (Right(rows), (Left(err), index)) => Left(List(errWithLineNumber(index + 1, err)))
        case (errs@Left(_), (Right(_), _)) => errs
        case (Left(errs), (Left(err), index)) => Left(errWithLineNumber(index + 1, err) +: errs)
      }
    }
  }

  private[model] def processLine(line: String): Either[String, RowType] = {
    line.split(",").toList match {
      case key :: AsInt(value) :: Nil => Right(key -> value)
      case key :: value :: Nil => Left(s"Could not convert $value to an Int")
      case l => Left(s"Wrong number of fields. Expected 2 but got ${l.length}")
    }
  }
}
