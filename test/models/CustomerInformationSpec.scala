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
        customerInfoJson(MTDfBMandated.value, isInsolvent = false).as[CustomerInformation] shouldBe customerInfoModel(MTDfBMandated,
          isInsolvent = false, continueToTrade = Some(true))
      }

      "there is a mandation status of MTDfB Voluntary" in {
          customerInfoJson(MTDfBVoluntary.value, isInsolvent = false).as[CustomerInformation] shouldBe customerInfoModel(MTDfBVoluntary,
            isInsolvent = false, continueToTrade = Some(true))
        }

      "there is a mandation status of Non MTDfB" in {
          customerInfoJson(NonMTDfB.value, isInsolvent = false).as[CustomerInformation] shouldBe customerInfoModel(NonMTDfB,
            isInsolvent = false, continueToTrade = Some(true))
        }

      }

    "there is a pending mandation status" in {
        customerInfoJsonPending.as[CustomerInformation] shouldBe customerInfoModelPending
    }

    "the user is insolvent and not continuing to trade" should {

      "return true for a user with no insolvency type" in {
        customerInfoModelInsolvent.isInsolventWithoutAccess shouldBe true
      }

      "return true for a user with a blocked insolvency type" in {
        customerInfoModelInsolvent.copy(insolvencyType = Some("09")).isInsolventWithoutAccess shouldBe true
      }

      "return false for a user with an allowed insolvency type" in {
        customerInfoModelInsolvent.copy(insolvencyType = Some("12")).isInsolventWithoutAccess shouldBe false
      }
    }

    "the user is insolvent but is continuing to trade" should {

      "return false for a user with no insolvency type" in {
        customerInfoModelInsolvent.copy(continueToTrade = Some(true)).isInsolventWithoutAccess shouldBe false
      }

      "return true for a user with a blocked insolvency type" in {
        customerInfoModelInsolvent.copy(continueToTrade = Some(true), insolvencyType = Some("10")).isInsolventWithoutAccess shouldBe true
      }

      "return false for a user with an allowed insolvency type" in {
        customerInfoModelInsolvent.copy(continueToTrade = Some(true), insolvencyType = Some("14")).isInsolventWithoutAccess shouldBe false
      }
    }

    "the user is not insolvent, regardless of the continueToTrade flag" in {
      customerNotInsolvent.isInsolventWithoutAccess shouldBe false
      customerNotInsolvent.copy(continueToTrade = Some(false)).isInsolventWithoutAccess shouldBe false
      customerNotInsolvent.copy(continueToTrade = None).isInsolventWithoutAccess shouldBe false
    }

    "fail to parse from JSON" when {

      "the JSON is in an invalid format" in {
        intercept[Exception](customerInfoJsonInvalid.as[CustomerInformation])
      }

      "there is a mandation status that is not recognised" in {
        intercept[Exception](customerInfoJson("VATDEC Mandatory", isInsolvent = false).as[CustomerInformation])
      }
    }

  }
}
