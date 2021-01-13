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

package assets

import models.{CustomerInformation, MTDfBMandated, MandationStatus}
import play.api.libs.json.{JsObject, Json}

object CustomerInformationConstants {


  def customerInfoJson(mandationStatus: String, isInsolvent: Boolean): JsObject = Json.obj(
    "mandationStatus" -> mandationStatus,
    "customerDetails" -> Json.obj(
    "isInsolvent" -> isInsolvent,
    "continueToTrade" -> true
  ))

  val customerInfoJsonPending: JsObject = customerInfoJson("MTDfB Mandated", false) ++ Json.obj(
    "pendingChanges" -> Json.obj(
      "mandationStatus" -> "Non MTDfB"
    )
  )

  val customerInfoJsonInvalid: JsObject = Json.obj(

    "mandationStatusInvalid" -> "MTDfB Mandated"
  )

  def customerInfoModel(mandationStatus: MandationStatus, isInsolvent: Boolean, continueToTrade: Option[Boolean]): CustomerInformation =
    CustomerInformation(  mandationStatus, isInsolvent, continueToTrade, inflightMandationStatus = false)

  val customerInfoModelPending =
    CustomerInformation( MTDfBMandated, isInsolvent = false, continueToTrade = Some(true), inflightMandationStatus = true)

  val customerInfoModelInsolvent =
    CustomerInformation( MTDfBMandated, isInsolvent = true, continueToTrade = Some(false), inflightMandationStatus = false)
}
