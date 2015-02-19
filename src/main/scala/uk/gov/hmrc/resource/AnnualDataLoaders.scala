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
package uk.gov.hmrc.resource

import uk.gov.hmrc.model.yeardata._
import scala.io.Source


object AnnualConstantsLoader {
  def loadData(input: Source): Either[List[String], Map[String, Int]] = {
    AnnualConstants.fromCSVLines(input.getLines().toList).fold(
      errs => Left(errs),
      pairs => Right(Map(pairs: _*))
    )
  }
}

object BasicPercentagesLoader {
  def loadData(input: Source): Either[List[String], BasicPercentages] = {
    BasicPercentage.fromCSVLines(input.getLines().toList).fold(
      errs => Left(errs),
      values => Right(BasicPercentages(values))
    )
  }
}


object CarBenefitsPre98Loader {
  def loadData(input: Source): Either[List[String], CarBenefitsPre98] = {
    CarBenefitPercentagePre98.fromCSVLines(input.getLines().toList).fold(
      errs => Left(errs),
      values => Right(CarBenefitsPre98(values))
    )
  }
}

object CarBenefitsPost98Loader {
  def loadData(input: Source): Either[List[String], CarBenefitsPost98] = {
    CarBenefitPercentagesPost98.fromCSVLines(input.getLines().toList).fold(
      errs => Left(errs),
      values => Right(CarBenefitsPost98(values))
    )
  }
}

object CarBenefitsZELoader {
  def loadData(input: Source): Either[List[String], Int] = {
    val lines = input.getLines().toList

    lines match {
      case line :: Nil => CarBenefitPercentageZE.processLine(line)
      case Nil => Left(List("ZE data is empty"))
      case _ => Left(List("There should only be a single value in the ZE data"))
    }
  }
}