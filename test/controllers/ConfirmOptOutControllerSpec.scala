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
import org.scalatest.BeforeAndAfterAll
import play.api.http.Status
import play.api.test.Helpers._
import common.Constants._
import org.jsoup.Jsoup

class ConfirmOptOutControllerSpec extends ControllerBaseSpec with BeforeAndAfterAll {

  val testYesRadioSelection: String = "yes"
  val testNoRadioSelection: String = "no"

  def controller: ConfirmOptOutController = new ConfirmOptOutController(
    messagesApi,
    mockAuthPredicate,
    appConfig
  )

  "calling the show action" should {

    mockAgentAuthorised()

    lazy val result = controller.show()(requestWithClientVRN)

    "return 200" in {
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      contentType(result) shouldBe Some("text/html")
      charset(result) shouldBe Some("utf-8")
    }

  }

  "calling the submit action" when {

    lazy val testRequest = requestWithClientVRN.withFormUrlEncodedBody("confirmOptOut" -> optionYes)

    "the user selects yes and submits" should {

      lazy val result = controller.submit()(testRequest)

      "add the yes answer to session" in {

        session(result).get(SessionKeys.confirmOptOut) shouldBe Some(testYesRadioSelection)
      }
      //TODO: Update this test when confirmation page is in
      "redirect to the opt out confirmation page" in {

        status(result) shouldBe Status.SEE_OTHER

        redirectLocation(result) shouldBe
          Some(controllers.routes.ConfirmOptOutController.show().url)
      }
    }

    "the user selects no and submits" should {

      lazy val result = controller.submit()(requestWithClientVRN.withFormUrlEncodedBody("confirmOptOut" -> optionNo))

      "add the no answer to session" in {

        session(result).get(SessionKeys.confirmOptOut) shouldBe Some(testNoRadioSelection)
      }

      //TODO: Update this test when decided not to opt out page is in
      "redirect to the you have decided not to opt out page" in {

        status(result) shouldBe Status.SEE_OTHER

        redirectLocation(result) shouldBe
          Some(controllers.routes.ConfirmOptOutController.show().url)
      }
    }

    "the user doesn't select an option and submits" should {

      lazy val result = controller.submit()(requestWithClientVRN)

      "display an error page" in {

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
