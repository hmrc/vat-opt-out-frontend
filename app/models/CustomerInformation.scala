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

import play.api.libs.functional.syntax._
import play.api.libs.json._

case class CustomerInformation(mandationStatus: MandationStatus,
                               isInsolvent: Boolean,
                               continueToTrade: Option[Boolean],
                               inflightMandationStatus: Boolean) {

  val isInsolventWithoutAccess: Boolean = continueToTrade match {
    case Some(false) => isInsolvent
    case _ => false
  }
}

object CustomerInformation {

  def construct(mandationStatus: MandationStatus,
                isInsolvent: Boolean,
                continueToTrade: Option[Boolean],
                inflightMandationStatus: Option[String]): CustomerInformation = {

    val isMandationStatusPending = inflightMandationStatus.fold(false)(_ => true)
    CustomerInformation( mandationStatus, isInsolvent, continueToTrade, isMandationStatusPending)
  }

  implicit val reads: Reads[CustomerInformation] = (
    (JsPath \ "mandationStatus").read[MandationStatus] and
    (JsPath \ "customerDetails" \ "isInsolvent").read[Boolean] and
    (JsPath \ "customerDetails" \ "continueToTrade").readNullable[Boolean].orElse(Reads.pure(None)) and
    (JsPath \ "pendingChanges" \ "mandationStatus").readNullable[String].orElse(Reads.pure(None))
  )(construct _)
}
