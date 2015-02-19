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


trait FuelCodeBasedPercentage {
  def typeAPercentage: Int

  def typeDPercentage: Int

  def percentageForFuelCode(fuelCode: FuelCode): Int = {
    fuelCode match {
      case FuelTypes.FuelCodeDiesel => typeAPercentage
      case FuelTypes.FuelCodeOther => typeDPercentage
    }
  }
}
