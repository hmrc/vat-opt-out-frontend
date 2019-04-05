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

package assets

import models.{CustomerInformation, MTDfBMandated, MandationStatus}
import play.api.libs.json.{JsObject, Json}

object CustomerInformationConstants {

  val customerInfoJsonAll: JsObject = Json.obj(
    "tradingName" -> "ChoC Services",
    "organisationName" -> "ChoC Services Ltd",
    "firstName" -> "Chocolate",
    "lastName" -> "Services",
    "mandationStatus" -> "MTDfB Mandated"
  )

  val customerInfoJsonOrg: JsObject = Json.obj(
    "organisationName" -> "ChoC Services Ltd",
    "mandationStatus" -> "MTDfB Mandated"
  )

  val customerInfoJsonInd: JsObject = Json.obj(
    "firstName" -> "Chocolate",
    "lastName" -> "Services",
    "mandationStatus" -> "MTDfB Mandated"
  )

  def customerInfoJsonNoName(mandationStatus: String): JsObject = Json.obj(
    "mandationStatus" -> mandationStatus
  )

  val customerInfoJsonPending: JsObject = customerInfoJsonAll ++ Json.obj(
    "pendingChanges" -> Json.obj(
      "mandationStatus" -> "Non MTDfB"
    )
  )

  val customerInfoJsonInvalid: JsObject = Json.obj(
    "tradingName" -> true,
    "mandationStatus" -> "MTDfB Mandated"
  )

  val customerInfoModelTradeName =
    CustomerInformation(Some("ChoC Services"), MTDfBMandated, inflightMandationStatus = false)

  val customerInfoModelOrgName =
    CustomerInformation(Some("ChoC Services Ltd"),  MTDfBMandated, inflightMandationStatus = false)

  val customerInfoModelIndName =
    CustomerInformation(Some("Chocolate Services"),  MTDfBMandated, inflightMandationStatus = false)

  def customerInfoModelNoName(mandationStatus: MandationStatus): CustomerInformation =
    CustomerInformation(None,  mandationStatus, inflightMandationStatus = false)

  val customerInfoModelPending =
    CustomerInformation(Some("ChoC Services"), MTDfBMandated, inflightMandationStatus = true)
}
