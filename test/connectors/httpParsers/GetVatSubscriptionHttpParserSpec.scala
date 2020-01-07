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

import assets.BaseTestConstants.errorModel
import assets.CustomerInformationConstants._
import connectors.httpParsers.GetVatSubscriptionHttpParser.{GetVatSubscriptionReads, GetVatSubscriptionResponse}
import models.{ErrorModel, MTDfBMandated}
import play.api.libs.json.Json
import play.mvc.Http.Status
import uk.gov.hmrc.http.HttpResponse
import utils.TestUtils

class GetVatSubscriptionHttpParserSpec extends TestUtils {

  def vatSubscriptionResult(response: HttpResponse): GetVatSubscriptionResponse =
    GetVatSubscriptionReads.read("", "", response)

  "GetVatSubscriptionReads" when {

    "the HTTP response status is 200 (OK)" when {

      "valid JSON is returned" should {

        "return a CustomerInformation model" in {
          val response = HttpResponse(Status.OK, Some(customerInfoJson("MTDfB Mandated")))
          val result = vatSubscriptionResult(response)
          result shouldBe Right(customerInfoModel(MTDfBMandated))
        }
      }

      "invalid JSON is returned" should {

        "return an Error model with status code of 500 (INTERNAL_SERVER_ERROR)" in {
          val response = HttpResponse(Status.OK, Some(Json.obj("mandationStatus" -> true)))
          val result = vatSubscriptionResult(response)
          result shouldBe Left(errorModel.copy(body = "The endpoint returned invalid JSON."))
        }
      }
    }

    "the HTTP response is not 200 (OK)" should {

      "return an error model" in {
        val jsonResponse = Json.obj("fail" -> "nope")
        val response = HttpResponse(Status.NOT_FOUND, Some(jsonResponse))
        val result = vatSubscriptionResult(response)
        result shouldBe Left(ErrorModel(Status.NOT_FOUND, Json.prettyPrint(jsonResponse)))
      }
    }
  }
}
