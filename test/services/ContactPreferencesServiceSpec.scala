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

package services

import assets.BaseTestConstants._
import assets.ContactPreferencesConstants.contactPreferenceModelDigital
import mocks.MockContactPreferencesConnector
import utils.TestUtils

class ContactPreferencesServiceSpec extends TestUtils with MockContactPreferencesConnector {

  val service = new ContactPreferencesService(connector)

  "Calling .getContactPreferences" when {

    "the connector returns a ContactPreferences model" should {

      "return the model" in {
        mockContactPreferencesSuccess()

        val result = await(service.getContactPreferences(testVrn))
        result shouldBe Right(contactPreferenceModelDigital)
      }
    }

    "the connector returns an error model" should {

      "return the error model" in {
        mockContactPreferencesFailure()

        val result = await(service.getContactPreferences(testVrn))
        result shouldBe Left(errorModel)
      }
    }
  }
}
