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

package audit.models

import models.MTDfBMandated
import play.api.libs.json.Json
import utils.TestUtils

class GetCustomerInfoAuditModelSpec extends TestUtils {

  val auditModel = GetCustomerInfoAuditModel(
    "123456789",
    Some("XARN1234567"),
    isAgent = true,
    MTDfBMandated,
    inflightMandationStatus = true
  )

  "The GetCustomerInfoAuditModel" should {

    "have the correct transaction name" in {
      auditModel.transactionName shouldBe "view-vat-subscription-details"
    }

    "have the correct audit type" in {
      auditModel.auditType shouldBe "getVatSubscriptionDetails"
    }

    "have the correct audit details" in {
      auditModel.detail shouldBe Json.obj(
        "vrn" -> "123456789",
        "arn" -> "XARN1234567",
        "isAgent" -> true,
        "mandationStatus" -> "1",
        "inflightMandationStatus" -> true
      )
    }
  }
}
