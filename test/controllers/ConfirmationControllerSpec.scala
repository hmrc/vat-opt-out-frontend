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
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.MockAuth
import views.html.ConfirmationView

class ConfirmationControllerSpec extends MockAuth {

  implicit val confirmationView: ConfirmationView = injector.instanceOf[ConfirmationView]

  def controller: ConfirmationController = new ConfirmationController(
    mockAuthPredicate, mockErrorHandler, confirmationView
  )

  ".show()" when {

    insolvencyCheck(controller.show())

    "the user has opted out successfully" should {

      lazy val optedOutRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest().withSession(
        SessionKeys.inflightMandationStatus -> "true",
        SessionKeys.optOutSuccessful -> "true",
        SessionKeys.insolventWithoutAccessKey -> "false"
      )
      lazy val result = controller.show()(optedOutRequest)

      "return 200" in {
        mockIndividualAuthorised()
        status(result) shouldBe Status.OK
      }

      "return HTML" in {
        contentType(result) shouldBe Some("text/html")
        charset(result) shouldBe Some("utf-8")
      }
    }

    "the user has not opted out yet" should {

      lazy val result = controller.show()(requestPredicatedClient)

      "return 500" in {
        mockIndividualAuthorised()
        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      }

      "return HTML" in {
        contentType(result) shouldBe Some("text/html")
        charset(result) shouldBe Some("utf-8")
      }
    }
  }
}
