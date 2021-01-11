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

package controllers

import common.SessionKeys
import play.api.http.Status
import play.api.test.Helpers._
import utils.MockAuth
import common.Constants.{optionNo, optionYes}
import views.html.TurnoverThresholdView

class TurnoverThresholdControllerSpec extends MockAuth {

  implicit val turnoverThresholdView: TurnoverThresholdView = injector.instanceOf[TurnoverThresholdView]
  val controller = new TurnoverThresholdController(mockAuthPredicate, mockOptOutPredicate, turnoverThresholdView)

  ".show() with no turnover value in session" should {

    lazy val result = controller.show()(requestPredicatedClient)

    "return 200" in {
      mockIndividualAuthorised()
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      contentType(result) shouldBe Some("text/html")
      charset(result) shouldBe Some("utf-8")
    }
  }

  ".show() with a turnover value in session" should {

    lazy val result = controller.show()(requestPredicatedClient.withSession(common.SessionKeys.turnoverThreshold -> optionYes))

    "return 200" in {
      mockIndividualAuthorised()
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      contentType(result) shouldBe Some("text/html")
      charset(result) shouldBe Some("utf-8")
    }
  }

  "Calling the submit action" when {

    "a user is enrolled with a valid enrolment" when {

      "the form is successfully submitted with option yes" should {

        lazy val result = controller.submit(requestPredicatedClient.withFormUrlEncodedBody("threshold" -> optionYes))

        "redirect to the correct view" in {
          status(result) shouldBe Status.SEE_OTHER
          redirectLocation(result) shouldBe Some(routes.CannotOptOutController.show().url)
        }

        "add the threshold to the session" in {
          session(result).get(SessionKeys.turnoverThreshold) shouldBe Some(optionYes)
        }
      }

      "the form is successfully submitted with option no" should {

        lazy val result = controller.submit(requestPredicatedClient.withFormUrlEncodedBody("threshold" -> optionNo))

        "redirect to the correct view" in {
          status(result) shouldBe Status.SEE_OTHER
          redirectLocation(result) shouldBe Some(routes.ConfirmOptOutController.show().url)
        }

        "add the new threshold value to session" in {
          session(result).get(SessionKeys.turnoverThreshold) shouldBe Some(optionNo)
        }
      }

      "the form is successfully submitted with no option selected" should {

        lazy val result = controller.submit(requestPredicatedClient.withFormUrlEncodedBody())

        "return to the view with a bad request" in {
          status(result) shouldBe Status.BAD_REQUEST
        }

        "return HTML" in {
          contentType(result) shouldBe Some("text/html")
          charset(result) shouldBe Some("utf-8")
        }
      }
    }
  }
}
