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

import scala.util.Try



trait CSVProcessor {

  protected object AsInt {
    def unapply(s: String): Option[Int] = Try {
      s.trim.toInt
    }.toOption
  }

  type RowType
  type ErrorsOrRows = Either[List[String], List[RowType]]

  protected def processLine(line: String): Either[List[String], RowType]

  protected def toFields(line: String) = line.split(",").toList

  protected def badIntsErrors(fields: List[String]): List[String] = {
    val failedFields = fields.zipWithIndex.filter(x => AsInt.unapply(x._1).isEmpty)
    failedFields.map(f => s"Field ${f._2 + 1}: '${f._1}' is not an Int")
  }

  def fromCSVLines(lines: List[String]): ErrorsOrRows = {
    val parsedLines = lines.map(processLine).zipWithIndex

    def errWithLineNumber(lineNumber: Int, err: String): String = s"line $lineNumber: $err"

    parsedLines.foldRight[ErrorsOrRows](Right(List())) {
      (elem, acc) =>
        (acc, elem) match {
          case (Right(rows), (Right(row), _)) => Right(row :: rows)
          case (Right(rows), (Left(errs), index)) => Left(errs.map(err => errWithLineNumber(index + 1, err)))
          case (errs@Left(_), (Right(_), _)) => errs
          case (Left(errs), (Left(lineErrs), index)) => Left(lineErrs.map(err => errWithLineNumber(index + 1, err)) ++: errs)
        }
    }
  }
}
