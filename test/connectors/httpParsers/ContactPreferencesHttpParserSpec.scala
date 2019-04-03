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

package connectors.httpParsers

import assets.BaseTestConstants.errorModel
import assets.ContactPreferencesConstants._
import connectors.httpParsers.ContactPreferencesHttpParser.{ContactPreferenceReads, ContactPreferencesResponse}
import models.ErrorModel
import play.api.libs.json.Json
import play.mvc.Http.Status
import uk.gov.hmrc.http.HttpResponse
import utils.TestUtils

class ContactPreferencesHttpParserSpec extends TestUtils {

  def contactPreferencesResult(response: HttpResponse): ContactPreferencesResponse =
    ContactPreferenceReads.read("", "", response)

  "ContactPreferencesReads" when {

    "the HTTP response status is 200 (OK)" when {

      "valid JSON is returned" should {

        "return a ContactPreferences model" in {
          val response = HttpResponse(Status.OK, Some(contactPreferencesJsonMax))
          val result = contactPreferencesResult(response)
          result shouldBe Right(contactPreferenceModelDigital)
        }
      }

      "invalid JSON is returned" should {

        "return an Error model with status code of 500 (INTERNAL_SERVER_ERROR)" in {
          val response = HttpResponse(Status.OK, Some(Json.obj("preference" -> true)))
          val result = contactPreferencesResult(response)
          result shouldBe Left(errorModel.copy(body = "The endpoint returned invalid JSON."))
        }
      }
    }

    "the HTTP response is not 200 (OK)" should {

      "return an error model" in {
        val jsonResponse = Json.obj("invalid" -> "nothing")
        val response = HttpResponse(Status.NOT_FOUND, Some(jsonResponse))
        val result = contactPreferencesResult(response)
        result shouldBe Left(ErrorModel(Status.NOT_FOUND, Json.prettyPrint(jsonResponse)))
      }
    }
  }
}
