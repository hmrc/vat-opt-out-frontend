/*
 * Copyright 2020 HM Revenue & Customs
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

package connectors.httpParsers

import connectors.httpParsers.UpdateVatSubscriptionHttpParser.{UpdateVatSubscriptionReads, UpdateVatSubscriptionResponse}
import models.{ErrorModel, UpdateVatSubscription}
import play.api.http.Status
import play.api.libs.json.Json
import uk.gov.hmrc.http.HttpResponse
import utils.TestUtils

class UpdateVatSubscriptionHttpParserSpec extends TestUtils {

  def parserResult(response: HttpResponse): UpdateVatSubscriptionResponse =
    UpdateVatSubscriptionReads.read("", "", response)

  "UpdateVatSubscriptionReads" when {

    "the HTTP response is OK (200)" when {

      "valid JSON is returned" should {

        "return a UpdateVatSubscription model" in {
          val response = HttpResponse(Status.OK, Some(Json.obj("formBundle" -> "0123456789")))
          val result = parserResult(response)
          result shouldBe Right(UpdateVatSubscription("0123456789"))
        }
      }

      "invalid JSON is returned" should {

        "return an Error Model with a status code of INTERNAL_SERVER_ERROR (500)" in {
          val response = HttpResponse(Status.OK, Some(Json.obj("formBundle" -> true)))
          val result = parserResult(response)
          result shouldBe Left(ErrorModel(Status.INTERNAL_SERVER_ERROR, "The endpoint returned invalid JSON."))
        }
      }
    }

    "the HTTP response is something unexpected" should {

      "return an Error Model with the status code and message of the error" in {
        val jsonResponse = Json.obj("CONFLICT" -> "There has been a conflict!")
        val response = HttpResponse(Status.CONFLICT, Some(jsonResponse))
        val result = parserResult(response)
        result shouldBe Left(ErrorModel(Status.CONFLICT, Json.prettyPrint(jsonResponse)))
      }
    }
  }
}
