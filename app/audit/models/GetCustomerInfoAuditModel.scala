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

import models.MandationStatus
import play.api.libs.json.{Format, JsValue, Json}

case class GetCustomerInfoAuditModel(vrn: String,
                                     arn: Option[String],
                                     isAgent: Boolean,
                                     mandationStatus: MandationStatus,
                                     inflightMandationStatus: Boolean) extends AuditModel {

  override val transactionName: String = "view-vat-subscription-details"
  override val auditType: String = "getVatSubscriptionDetails"
  override val detail: JsValue = Json.toJson(this)
}

object GetCustomerInfoAuditModel {
  implicit val format: Format[GetCustomerInfoAuditModel] = Json.format[GetCustomerInfoAuditModel]
}
