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

import mocks.{MockContactPreferencesConnector, MockContactPreferencesService}
import play.api.http.Status
import play.api.test.Helpers._
import utils.{MockAuth, TestUtils}
import models.{ContactPreferences, ErrorModel}
import common.Constants.{preferenceDigital, preferenceFail, preferencePaper}
import play.api.mvc.Result

import scala.concurrent.Future

class ConfirmationControllerSpec extends MockAuth with MockContactPreferencesService with TestUtils{

  def controller: ConfirmationController = new ConfirmationController(
    messagesApi, mockAuthPredicate, mockContactPreferencesService
  )(appConfig, ec)

  ".show() for an individual with digital preference" should {

    mockIndividualAuthorised()
    mockGetContactPreferences("123456789")(Future(Right(ContactPreferences(preferenceDigital))))
    lazy val result = controller.show()(requestWithClientVRN)

    "return 200" in {
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      contentType(result) shouldBe Some("text/html")
      charset(result) shouldBe Some("utf-8")
    }
  }

  ".show() for an individual with paper preference" should {

    mockIndividualAuthorised()
    mockGetContactPreferences("123456789")(Future(Right(ContactPreferences(preferencePaper))))
    lazy val result = controller.show()(requestWithClientVRN)

    "return 200" in {
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      contentType(result) shouldBe Some("text/html")
      charset(result) shouldBe Some("utf-8")
    }
  }

  ".show() for an individual where preference could not be retrieved from service" should {

    mockIndividualAuthorised()
    mockGetContactPreferences("123456789")(Future(Left(ErrorModel(NOT_FOUND, "Couldn't find a user with VRN provided"))))
    lazy val result: Future[Result] = controller.show()(requestWithClientVRN)

    "return 200" in {
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      contentType(result) shouldBe Some("text/html")
      charset(result) shouldBe Some("utf-8")
    }
  }

// //TODO: Uncomment this test when agents are added to the auth predicate to allow agents
//  ".show() for a transactor with digital preference" should {
//
//    mockAgentAuthorised()
//    mockGetContactPreferences("123456789")(Future(Right(ContactPreferences(preferenceDigital))))
//    lazy val result = controller.show()(requestWithBusinessNameAndClientVRN)
//
//
//    "return 200" in {
//      status(result) shouldBe Status.OK
//    }
//
//    "return HTML" in {
//      contentType(result) shouldBe Some("text/html")
//      charset(result) shouldBe Some("utf-8")
//    }
//  }
//
//  ".show() for an transactor with paper preference" should {
//
//    mockAgentAuthorised()
//    mockGetContactPreferences("123456789")(Future(Right(ContactPreferences(preferencePaper))))
//    lazy val result = controller.show()(requestWithBusinessNameAndClientVRN)
//
//
//    "return 200" in {
//      status(result) shouldBe Status.OK
//    }
//
//    "return HTML" in {
//      contentType(result) shouldBe Some("text/html")
//      charset(result) shouldBe Some("utf-8")
//    }
//  }
}
