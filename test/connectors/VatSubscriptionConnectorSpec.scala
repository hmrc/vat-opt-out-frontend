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

import org.scalatestplus.mockito.MockitoSugar
import uk.gov.hmrc.http.HttpClient
import utils.TestUtils

class VatSubscriptionConnectorSpec extends TestUtils with MockitoSugar {

  val connector = new VatSubscriptionConnector(mock[HttpClient], appConfig)

  "VatSubscriptionConnector" should {

    "generate the correct url for getCustomerInfo" in {
      connector.getCustomerInfoUrl("123456789") shouldBe "vat-subscription/vat-subscription/123456789/full-information"
    }

    "generate the correct url for updateMandationStatus" in {
      connector.updateMandationStatusUrl("123456789") shouldBe "vat-subscription/vat-subscription/123456789/mandation-status"
    }
  }
}
