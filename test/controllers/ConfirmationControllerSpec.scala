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

import mocks.MockContactPreferencesService
import play.api.http.Status
import play.api.test.Helpers._
import utils.{MockAuth, TestUtils}
import models.{ContactPreferences, ErrorModel}
import common.Constants.{preferenceDigital, preferencePaper}
import play.api.mvc.Result

import scala.concurrent.Future

class ConfirmationControllerSpec extends MockAuth with MockContactPreferencesService with TestUtils {

  def controller: ConfirmationController = new ConfirmationController(
    mockAuthPredicate, mockOptOutPredicate,
    mockContactPreferencesService
  )

  ".show() for a transactor with digital preference but no business name in session" should {

    lazy val result = controller.show()(requestMandatedClientVRNNoBusinessName)

    "return 200" in {
      mockAgentAuthorised()
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      contentType(result) shouldBe Some("text/html")
      charset(result) shouldBe Some("utf-8")
    }
  }

  ".show() for a transactor with digital preference (verified email in session)" should {

    lazy val result = controller.show()(requestPredicatedAgentDigital)

    "return 200" in {
      mockAgentAuthorised()
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      contentType(result) shouldBe Some("text/html")
      charset(result) shouldBe Some("utf-8")
    }
  }

  ".show() for an transactor with paper preference (no verified email in session)" should {

    lazy val result = controller.show()(requestPredicatedAgentPaper)

    "return 200" in {
      mockAgentAuthorised()
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      contentType(result) shouldBe Some("text/html")
      charset(result) shouldBe Some("utf-8")
    }
  }

  ".show() for an individual with digital preference" should {

    lazy val result = controller.show()(requestPredicatedClient)

    "return 200" in {
      mockIndividualAuthorised()
      mockGetContactPreferences("123456789")(Future(Right(ContactPreferences(preferenceDigital))))
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      contentType(result) shouldBe Some("text/html")
      charset(result) shouldBe Some("utf-8")
    }
  }

  ".show() for an individual with paper preference" should {

    lazy val result = controller.show()(requestPredicatedClient)

    "return 200" in {
      mockIndividualAuthorised()
      mockGetContactPreferences("123456789")(Future(Right(ContactPreferences(preferencePaper))))
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      contentType(result) shouldBe Some("text/html")
      charset(result) shouldBe Some("utf-8")
    }
  }

  ".show() for an individual where preference could not be retrieved from service" should {

    lazy val result: Future[Result] = controller.show()(requestPredicatedClient)

    "return 200" in {
      mockIndividualAuthorised()
      mockGetContactPreferences("123456789")(Future(Left(ErrorModel(NOT_FOUND, "Couldn't find a user with VRN provided"))))
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      contentType(result) shouldBe Some("text/html")
      charset(result) shouldBe Some("utf-8")
    }
  }
}
