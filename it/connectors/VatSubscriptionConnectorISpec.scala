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

package connectors

import connectors.httpParsers.GetVatSubscriptionHttpParser.GetVatSubscriptionResponse
import connectors.httpParsers.UpdateVatSubscriptionHttpParser.UpdateVatSubscriptionResponse
import helpers.IntegrationBaseSpec
import models._
import play.api.http.Status.INTERNAL_SERVER_ERROR
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import stubs.VatSubscriptionStub

class VatSubscriptionConnectorISpec extends IntegrationBaseSpec {

  private trait Test {
    val connector: VatSubscriptionConnector = app.injector.instanceOf[VatSubscriptionConnector]
  }

  implicit val user: User[AnyContentAsEmpty.type] = User("999999999")(FakeRequest())

  "Calling .getCustomerInfo" when {

    "valid JSON is returned by the endpoint" should {

      "return a CustomerInformation model" in new Test {
        VatSubscriptionStub.stubCustomerInfo

        val expected = Right(CustomerInformation(MTDfBMandated, isInsolvent = false,
          continueToTrade = Some(true), inflightMandationStatus = false, None))
        val result: GetVatSubscriptionResponse = await(connector.getCustomerInfo("123456789"))

        result shouldBe expected
      }
    }

    "an internal server error is returned by the endpoint" should {

      "return an error model" in new Test {
        VatSubscriptionStub.stubCustomerInfoError

        val expected = Left(ErrorModel(INTERNAL_SERVER_ERROR, """{"fail":"nope"}"""))
        val result: GetVatSubscriptionResponse = await(connector.getCustomerInfo("123456789"))

        result shouldBe expected
      }
    }
  }

  "Calling .updateMandationStatus" when {

    "valid JSON is returned by the endpoint" should {

      "return an UpdateVatSubscription model" in new Test {
        VatSubscriptionStub.stubUpdateVatSubscription

        val expected = Right(UpdateVatSubscription("0123456789"))
        val result: UpdateVatSubscriptionResponse = await(connector.updateMandationStatus("123456789", MTDfBMandated))

        result shouldBe expected
      }
    }

    "an internal server error is returned by the endpoint" should {

      "return an Error Model" in new Test {
        VatSubscriptionStub.stubUpdateVatSubscriptionError

        val expected = Left(ErrorModel(INTERNAL_SERVER_ERROR, """{"fail":"nope"}"""))
        val result: UpdateVatSubscriptionResponse = await(connector.updateMandationStatus("123456789", MTDfBMandated))

        result shouldBe expected
      }
    }
  }
}
