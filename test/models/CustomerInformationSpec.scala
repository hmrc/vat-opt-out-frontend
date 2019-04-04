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

package models

import assets.CustomerInformationConstants._
import play.api.libs.json.Json
import utils.TestUtils

class CustomerInformationSpec extends TestUtils {

  "CustomerInformation" should {

    "successfully parse from JSON" when {

      "all expected fields are present" in {
        customerInfoJsonAll.as[CustomerInformation] shouldBe customerInfoModelTradeName
      }

      "there is only an organisation name present" in {
        customerInfoJsonOrg.as[CustomerInformation] shouldBe customerInfoModelOrgName
      }

      "there is only a first and last name present" in {
        customerInfoJsonInd.as[CustomerInformation] shouldBe customerInfoModelIndName
      }

      "there are no names present" in {
        customerInfoJsonNoName.as[CustomerInformation] shouldBe customerInfoModelEmpty
      }

      "there is a pending Mandation Status" in {
        customerInfoJsonPending.as[CustomerInformation] shouldBe customerInfoModelPending
      }
    }

    "fail to parse from JSON" when {

      "the JSON is invalid" in {
        intercept[Exception](customerInfoJsonInvalid.as[CustomerInformation])
      }
    }
  }
}
