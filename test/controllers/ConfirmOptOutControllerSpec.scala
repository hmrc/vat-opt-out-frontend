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

import assets.BaseTestConstants.{errorModel, updateVatSubscriptionModel}
import common.SessionKeys.{inflightMandationStatus, mandationStatus}
import connectors.httpParsers.UpdateVatSubscriptionHttpParser.UpdateVatSubscriptionResponse
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.mockito.stubbing.OngoingStubbing
import play.api.http.Status
import play.api.test.Helpers._
import utils.MockAuth

import scala.concurrent.Future

class ConfirmOptOutControllerSpec extends MockAuth {

  def vatSubscriptionUpdateSetUp(result: UpdateVatSubscriptionResponse): OngoingStubbing[Future[UpdateVatSubscriptionResponse]] = {
    reset(mockVatSubscriptionService)
    when(mockVatSubscriptionService.updateMandationStatus(any(), any())(any(), any()))
      .thenReturn(Future.successful(result))
  }


  val controller = new ConfirmOptOutController(mockAuthPredicate, mockOptOutPredicate, mockErrorHandler, mockVatSubscriptionService)

  ".show() for an individual fulfilling predicate sessions checks" should {

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

  ".show() for an agent fulfilling predicate sessions checks" should {

    lazy val result = controller.show()(requestPredicatedAgent)

    "return 200" in {
      mockAgentAuthorised()
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      contentType(result) shouldBe Some("text/html")
      charset(result) shouldBe Some("utf-8")
    }
  }

  "calling .updateMandationStatus() " when {

    "the Mandation Status has been updated Successfully" should {

      lazy val result = {
        mockIndividualAuthorised()
        vatSubscriptionUpdateSetUp(Right(updateVatSubscriptionModel))
        controller.updateMandationStatus()(requestPredicatedClient)
      }

      "return 303" in {
        status(result) shouldBe Status.SEE_OTHER
      }

      "Redirect to Confirmation Controller " in {
        redirectLocation(result) shouldBe Some(controllers.routes.ConfirmationController.show().url)
      }

      "Remove Mandation status from Session" in {
        session(result).get(mandationStatus) shouldBe None
      }

      "Update Inflight Mandation status to true" in {
        session(result).get(inflightMandationStatus) shouldBe Some("true")
      }

    }

    "the Mandation Status update was unsuccessful" should {

      lazy val result = {
        mockIndividualAuthorised()
        vatSubscriptionUpdateSetUp(Left(errorModel))
        controller.updateMandationStatus()(requestPredicatedClient)
      }

      "return 500" in  {
         status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      }

      "return HTML" in {
         contentType(result) shouldBe Some("text/html")
         charset(result) shouldBe Some("utf-8")
      }

    }

  }

}

