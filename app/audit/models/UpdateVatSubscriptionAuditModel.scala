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

package audit.models
import play.api.libs.json.{Format, JsValue, Json}

case class UpdateVatSubscriptionAuditModel(vrn: String,
                                           arn: Option[String],
                                           isAgent: Boolean) extends AuditModel {

  override val transactionName: String = "vat-opt-out"
  override val auditType: String = "VATOptOut"
  override val detail: JsValue = Json.toJson(this)
}

object UpdateVatSubscriptionAuditModel {
  implicit val format: Format[UpdateVatSubscriptionAuditModel] = Json.format[UpdateVatSubscriptionAuditModel]
}
