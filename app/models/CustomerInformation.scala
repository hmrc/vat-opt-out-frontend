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

import play.api.libs.functional.syntax._
import play.api.libs.json._

case class CustomerInformation(businessName: Option[String],
                               mandationStatus: MandationStatus,
                               inflightMandationStatus: Boolean)

object CustomerInformation {

  def construct(tradingName: Option[String],
                organisationName: Option[String],
                firstName: Option[String],
                lastName: Option[String],
                mandationStatus: MandationStatus,
                inflightMandationStatus: Option[String]): CustomerInformation = {

    val businessName = (tradingName, organisationName, firstName, lastName) match {
      case (Some(tradeName), _, _, _) => Some(tradeName)
      case (None, Some(orgName), _, _) => Some(orgName)
      case (None, None, Some(first), Some(last)) => Some(s"$first $last")
      case _ => None
    }
    val isMandationStatusPending = inflightMandationStatus.fold(false)(_ => true)

    CustomerInformation(businessName, mandationStatus, isMandationStatusPending)
  }

  implicit val reads: Reads[CustomerInformation] = (
    (JsPath \ "tradingName").readNullable[String] and
    (JsPath \ "organisationName").readNullable[String] and
    (JsPath \ "firstName").readNullable[String] and
    (JsPath \ "lastName").readNullable[String] and
    (JsPath \ "mandationStatus").read[MandationStatus] and
    (JsPath \ "pendingChanges" \ "mandationStatus").readNullable[String].orElse(Reads.pure(None))
  )(construct _)
}
