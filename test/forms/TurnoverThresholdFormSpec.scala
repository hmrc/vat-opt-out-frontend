/*
 * Copyright 2019 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package forms

import common.Constants
import play.api.data.FormError
import utils.TestUtils
import forms.TurnoverThresholdForm.turnoverThresholdForm

class TurnoverThresholdFormSpec extends TestUtils {

    "The turnoverThresholdForm" should {

    val testThresholdYes: String = Constants.optionYes
    val testThresholdNo: String = Constants.optionNo

    "validate that testThresholdYes is valid" in {
      val actual = turnoverThresholdForm(appConfig.thresholdAmount).bind(Map("threshold" -> testThresholdYes)).value
      actual shouldBe Some(testThresholdYes)
    }

    "validate that testThresholdNo is valid" in {
      val actual = turnoverThresholdForm(appConfig.thresholdAmount).bind(Map("threshold" -> testThresholdNo)).value
      actual shouldBe Some(testThresholdNo)
    }

    "validate that data has been entered" in {
      val formWithError = turnoverThresholdForm(appConfig.thresholdAmount).bind(Map[String,String]())
      formWithError.errors should contain(FormError("threshold", "turnoverThreshold.error.empty",Seq(appConfig.thresholdAmount)))
    }

  }
}
