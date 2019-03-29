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

import play.api.data.FormError
import utils.TestUtils
import forms.ConfirmOptOutForm._
import common.Constants._

class ConfirmOptOutFormSpec extends TestUtils {

  "ConfirmOptOutForm" when {

    "unbinding the form" when {

      "a radio option has not been selected" should {

        "produce the correct mapping" in {

          val formWithError = confirmOptOutForm.bind(Map(confirmOptOut -> ""))
          formWithError.errors should contain(FormError(confirmOptOut, "confirmOptOut.error.empty"))

        }
      }

      "the yes radio option has been selected" should {

        "produce the correct mapping" in {

          val form = confirmOptOutForm.fill(optionYes)

          form.data shouldBe Map(
            confirmOptOut -> optionYes
          )
        }
      }

      "the no radio button has been selected" should {

        "produce the correct mapping" in {

          val form = confirmOptOutForm.fill(optionNo)

          form.data shouldBe Map(
            confirmOptOut -> optionNo
          )
        }
      }
    }
  }
}
