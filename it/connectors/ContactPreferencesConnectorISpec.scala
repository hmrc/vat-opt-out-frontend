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

package connectors

import connectors.httpParsers.ContactPreferencesHttpParser.ContactPreferencesResponse
import helpers.IntegrationBaseSpec
import models.{ContactPreferences, ErrorModel}
import play.api.http.Status.INTERNAL_SERVER_ERROR
import stubs.ContactPreferencesStub

class ContactPreferencesConnectorISpec extends IntegrationBaseSpec {

  private trait Test {
    val connector: ContactPreferencesConnector = app.injector.instanceOf[ContactPreferencesConnector]
  }

  "Calling .getContactPreferences" when {

    "valid JSON is returned by the endpoint" should {

      "return a CustomerInformation model" in new Test {
        ContactPreferencesStub.stubContactPreferences

        val expected = Right(ContactPreferences("Digital"))
        val result: ContactPreferencesResponse = await(connector.getContactPreferences("123456789"))

        result shouldBe expected
      }
    }

    "an internal server error is returned by the endpoint" should {

      "return an error model" in new Test {
        ContactPreferencesStub.stubContactPreferencesError

        val expected = Left(ErrorModel(INTERNAL_SERVER_ERROR, """{"invalid":"nothing"}"""))
        val result: ContactPreferencesResponse = await(connector.getContactPreferences("123456789"))

        result shouldBe expected
      }
    }
  }
}
