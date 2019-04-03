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

package connectors

import connectors.httpParsers.VatSubscriptionHttpParser.VatSubscriptionResponse
import helpers.IntegrationBaseSpec
import models.{CustomerInformation, ErrorModel}
import play.api.http.Status.INTERNAL_SERVER_ERROR
import stubs.VatSubscriptionStub

class VatSubscriptionConnectorISpec extends IntegrationBaseSpec {

  private trait Test {
    val connector: VatSubscriptionConnector = app.injector.instanceOf[VatSubscriptionConnector]
  }

  "Calling .getCustomerInfo" when {

    "valid JSON is returned by the endpoint" should {

      "return a CustomerInformation model" in new Test {
        VatSubscriptionStub.stubCustomerInfo

        val expected = Right(CustomerInformation(Some("ChoC Services")))
        val result: VatSubscriptionResponse = await(connector.getCustomerInfo("123456789"))

        result shouldBe expected
      }
    }

    "an internal server error is returned by the endpoint" should {

      "return an error model" in new Test {
        VatSubscriptionStub.stubCustomerInfoError

        val expected = Left(ErrorModel(INTERNAL_SERVER_ERROR, """{"fail":"nope"}"""))
        val result: VatSubscriptionResponse = await(connector.getCustomerInfo("123456789"))

        result shouldBe expected
      }
    }
  }
}
