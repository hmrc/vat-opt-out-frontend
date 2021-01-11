/*
 * Copyright 2021 HM Revenue & Customs
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

package models

import assets.CustomerInformationConstants._
import utils.TestUtils

class CustomerInformationSpec extends TestUtils {

  "CustomerInformation" should {

    "successfully parse from JSON" when {

      "all expected fields are present and mandation status of MTDfBMandated" in {
        customerInfoJson(MTDfBMandated.value).as[CustomerInformation] shouldBe customerInfoModel(MTDfBMandated)
      }

      "there is a mandation status of MTDfB Voluntary" in {
          customerInfoJson(MTDfBVoluntary.value).as[CustomerInformation] shouldBe customerInfoModel(MTDfBVoluntary)
        }

      "there is a mandation status of Non MTDfB" in {
          customerInfoJson(NonMTDfB.value).as[CustomerInformation] shouldBe customerInfoModel(NonMTDfB)
        }

      }

    "there is a pending mandation status" in {
        customerInfoJsonPending.as[CustomerInformation] shouldBe customerInfoModelPending
    }

    "fail to parse from JSON" when {

      "the JSON is in an invalid format" in {
        intercept[Exception](customerInfoJsonInvalid.as[CustomerInformation])
      }

      "there is a mandation status that is not recognised" in {
        intercept[Exception](customerInfoJson("VATDEC Mandatory").as[CustomerInformation])
      }
    }
  }
}
