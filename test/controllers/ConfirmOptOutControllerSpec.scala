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

package controllers

import common.SessionKeys
import play.api.http.Status
import play.api.test.Helpers._
import common.Constants._
import org.jsoup.Jsoup
import utils.MockAuth

class ConfirmOptOutControllerSpec extends MockAuth {

  val testYesRadioSelection: String = "yes"
  val testNoRadioSelection: String = "no"

  def controller: ConfirmOptOutController = new ConfirmOptOutController(
    mockAuthPredicate,
    mockOptOutPredicate
  )

  "calling the show action" when {

    "there is no answer to the opt out confirmation in session" should {

      lazy val result = controller.show()(requestPredicatedClient)

      "return 200" in {
        mockAgentAuthorised()
        status(result) shouldBe Status.OK
      }

      "return HTML" in {
        contentType(result) shouldBe Some("text/html")
        charset(result) shouldBe Some("utf-8")
      }

    }

    "there is a yes answer to the opt out confirmation in session" should {

      lazy val result = controller.show()(requestPredicatedClient.withSession(SessionKeys.confirmOptOut -> optionYes))

      "return 200" in {
        mockIndividualAuthorised()
        status(result) shouldBe Status.OK
      }

      "return HTML" in {
        contentType(result) shouldBe Some("text/html")
        charset(result) shouldBe Some("utf-8")
      }
    }
  }

  "calling the submit action" when {

    lazy val testRequest = requestPredicatedClient.withFormUrlEncodedBody("confirmOptOut" -> optionYes)

    "the user selects yes and submits" should {

      lazy val result = controller.submit()(testRequest)

      "add the yes answer to session" in {

        session(result).get(SessionKeys.confirmOptOut) shouldBe Some(testYesRadioSelection)
      }

      "redirect to the opt out confirmation page" in {

        status(result) shouldBe Status.SEE_OTHER

        redirectLocation(result) shouldBe
          Some(controllers.routes.ConfirmationController.show().url)
      }
    }

    "the user selects no and submits" should {

      lazy val result = controller.submit()(requestPredicatedClient.withFormUrlEncodedBody("confirmOptOut" -> optionNo))

      "add the no answer to session" in {

        session(result).get(SessionKeys.confirmOptOut) shouldBe Some(testNoRadioSelection)
      }

      "redirect to the you have decided not to opt out page" in {

        status(result) shouldBe Status.SEE_OTHER

        redirectLocation(result) shouldBe
          Some(controllers.routes.DecidedNotToOptOutController.show().url)
      }
    }

    "the user doesn't select an option and submits" should {

      lazy val result = controller.submit()(requestPredicatedClient)

      "return a 400 BAD_REQUEST" in {

        status(result) shouldBe Status.BAD_REQUEST

      }

      "send the correct error text to the view" in {
        result map {
          r => Jsoup.parse(r.body.toString)
            .getElementById("confirmOptOut-error-summary").text() shouldBe "select an option"
        }
      }
    }
  }
}
