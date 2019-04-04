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

import models.CustomerInformation
import play.api.libs.json.{JsObject, Json}
object CustomerInformationConstants {

  val customerInfoJsonMax: JsObject = Json.obj(
    "tradingName" -> "ChoC Services",
    "organisationName" -> "ChoC Services Ltd",
    "firstName" -> "Chocolate",
    "lastName" -> "Services"
  )

  val customerInfoJsonOrg: JsObject = Json.obj(
    "organisationName" -> "ChoC Services Ltd"
  )

  val customerInfoJsonInd: JsObject = Json.obj(
    "firstName" -> "Chocolate",
    "lastName" -> "Services"
  )


  val customerInfoJsonInvalid: JsObject = Json.obj(
    "tradingName" -> true
  )

  val customerInfoModelTradeName = CustomerInformation(Some("ChoC Services"))
  val customerInfoModelOrgName= CustomerInformation(Some("ChoC Services Ltd"))
  val customerInfoModelIndName = CustomerInformation(Some("Chocolate Services"))
  val customerInfoModelEmpty = CustomerInformation(None)
}