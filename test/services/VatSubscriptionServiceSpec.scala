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

package services

import assets.BaseTestConstants._
import assets.CustomerInformationConstants.customerInfoModel
import mocks.MockVatSubscriptionConnector
import utils.TestUtils
import models.{MTDfBMandated, NonMTDfB, User}
import play.api.mvc.AnyContentAsEmpty

class VatSubscriptionServiceSpec extends TestUtils with MockVatSubscriptionConnector {

  val service = new VatSubscriptionService(connector)
  implicit val user: User[AnyContentAsEmpty.type] = clientUser

  "Calling .getCustomerInfo" when {

    "the connector returns a CustomerInformation model" should {

      "return the model" in {
        mockGetVatSubscriptionSuccess()

        val result = await(service.getCustomerInfo(testVrn))
        result shouldBe Right(customerInfoModel(MTDfBMandated, isInsolvent = false, continueToTrade = Some(true)))
      }
    }

    "the connector returns an error model" should {

      "return the error model" in {
        mockGetVatSubscriptionFailure()

        val result = await(service.getCustomerInfo(testVrn))
        result shouldBe Left(errorModel)
      }
    }
  }

  "Calling .updateMandationStatus" when {

    "the connector returns a UpdateVatSubscription model" should {

      "return the model" in {
        mockUpdateVatSubscriptionSuccess()

        val result = await(service.updateMandationStatus(testVrn,MTDfBMandated))
        result shouldBe Right(updateVatSubscriptionModel)
      }
    }

    "the connector returns an error model" should {

      "return the error model" in {
        mockUpdateVatSubscriptionFailure()

        val result = await(service.updateMandationStatus(testVrn,NonMTDfB))
        result shouldBe Left(errorModel)
      }
    }
  }
}
