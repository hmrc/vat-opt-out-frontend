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

package stubs

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import helpers.WireMockMethods
import play.api.http.Status.{INTERNAL_SERVER_ERROR, OK}
import play.api.libs.json.{JsObject, Json}

object VatSubscriptionStub extends WireMockMethods {

  private val getCustomerInfoUri: String = "/vat-subscription/([0-9]+)/full-information"
  private val updateMandationStatusUri: String = "/vat-subscription/([0-9]+)/mandation-status"

  def stubCustomerInfo: StubMapping = {
    when(method = GET, uri = getCustomerInfoUri)
      .thenReturn(status = OK, body = customerInfoJsonAll)
  }

  def stubCustomerInfoError: StubMapping = {
    when(method = GET, uri = getCustomerInfoUri)
      .thenReturn(status = INTERNAL_SERVER_ERROR, body = Json.obj("fail" -> "nope"))
  }

  def stubUpdateVatSubscription: StubMapping = {
    when(method = PUT, uri = updateMandationStatusUri)
      .thenReturn(status = OK, body = updateVatSubscriptionJson)
  }

  def stubUpdateVatSubscriptionError: StubMapping = {
    when(method = PUT, uri = updateMandationStatusUri)
      .thenReturn(status = INTERNAL_SERVER_ERROR, body = Json.obj("fail" -> "nope"))
  }

  val customerInfoJsonAll: JsObject = Json.obj(
    "customerDetails" -> Json.obj(
    "tradingName" -> "ChoC Services",
    "organisationName" -> "ChoC Services Ltd",
    "firstName" -> "Chocolate",
    "lastName" -> "Services"
     ),
    "mandationStatus" -> "MTDfB Mandated"
  )

  val updateVatSubscriptionJson: JsObject = Json.obj(
    "formBundle" -> "0123456789"
  )
}
