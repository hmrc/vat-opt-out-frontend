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

import assets.BaseTestConstants.{errorModel, updateVatSubscriptionModel}
import common.SessionKeys._
import connectors.httpParsers.UpdateVatSubscriptionHttpParser.UpdateVatSubscriptionResponse
import models.{ErrorModel, MTDfBMandated, NonMTDfB}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, verify, when}
import org.mockito.stubbing.OngoingStubbing
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.MockAuth
import views.html.ConfirmOptOutView

import scala.concurrent.Future

class ConfirmOptOutControllerSpec extends MockAuth {

  implicit val confirmOptOutView: ConfirmOptOutView = injector.instanceOf[ConfirmOptOutView]

  def vatSubscriptionUpdateSetUp(result: UpdateVatSubscriptionResponse): OngoingStubbing[Future[UpdateVatSubscriptionResponse]] = {
    reset(mockVatSubscriptionService)
    reset(mockAuditService)
    when(mockVatSubscriptionService.updateMandationStatus(any(), any())(any(), any(), any()))
      .thenReturn(Future.successful(result))
  }

  val controller = new ConfirmOptOutController(
    mockAuthPredicate, mockOptOutPredicate, mockErrorHandler, mockVatSubscriptionService, mockAuditService, confirmOptOutView
  )

  "Calling .show() for an individual" when {

    insolvencyCheck(controller.show())

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

  "calling .updateMandationStatus()" when {

    insolvencyCheck(controller.updateMandationStatus())

    "the mandation status has been updated successfully" should {

      lazy val request = FakeRequest().withSession(
        mandationStatus -> MTDfBMandated.value,
        insolventWithoutAccessKey -> "false",
        inflightMandationStatus -> "false",
        turnoverThreshold -> "no"
      )

      lazy val result = {
        mockIndividualAuthorised()
        vatSubscriptionUpdateSetUp(Right(updateVatSubscriptionModel))
        controller.updateMandationStatus()(request)
      }

      "return 303" in {
        status(result) shouldBe Status.SEE_OTHER
      }

      "redirect to Confirmation Controller " in {
        redirectLocation(result) shouldBe Some(controllers.routes.ConfirmationController.show().url)
      }

      "remove the answer to the turnover threshold question from session" in {
        session(result).get(turnoverThreshold) shouldBe None
      }

      "update the inflight mandation status session value to true" in {
        session(result).get(inflightMandationStatus) shouldBe Some("true")
      }

      "update the opt out successful session value to true" in {
        session(result).get(optOutSuccessful) shouldBe Some("true")
      }

      "update the mandation status session value to Non MTDfB" in {
        session(result).get(mandationStatus) shouldBe Some(NonMTDfB.value)
      }

      "send an audit event" in {
        verify(mockAuditService).audit(any(), any())(any(), any())
      }
    }

    "there is already a mandation status update in progress" when {

      "the user is a principal entity" should {

        lazy val result = {
          mockIndividualAuthorised()
          vatSubscriptionUpdateSetUp(Left(ErrorModel(CONFLICT, "conflict")))
          controller.updateMandationStatus()(requestPredicatedClient)
        }

        "return 303" in {
          status(result) shouldBe Status.SEE_OTHER
        }

        "redirect the user to the vat-overview page" in {
          redirectLocation(result) shouldBe Some(appConfig.vatSummaryServicePath)
        }
      }

      "the user is an agent" should {

        lazy val result = {
          mockAgentAuthorised()
          vatSubscriptionUpdateSetUp(Left(ErrorModel(CONFLICT, "conflict")))
          controller.updateMandationStatus()(requestPredicatedAgent)
        }

        "return 303" in {
          status(result) shouldBe Status.SEE_OTHER
        }

        "redirect the user to the chocs overview page" in {
          redirectLocation(result) shouldBe Some(appConfig.manageVatSubscriptionServicePath)
        }
      }
    }

    "the mandation status update was unsuccessful" should {

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
