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

package controllers.predicates

import common.SessionKeys
import connectors.httpParsers.GetVatSubscriptionHttpParser.GetVatSubscriptionResponse
import models._
import org.jsoup.Jsoup
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{never, verify, when, reset}
import play.api.http.Status
import play.api.mvc.AnyContentAsEmpty
import utils.MockAuth

import scala.concurrent.Future

class OptOutPredicateSpec extends MockAuth {

  def setup(result: GetVatSubscriptionResponse): Unit = {
    reset(mockVatSubscriptionService)
    reset(mockAuditService)
    when(mockVatSubscriptionService.getCustomerInfo(any())(any(), any()))
      .thenReturn(Future.successful(result))
  }

  def userWithSession(inflightMandation: Boolean, mandationStatus: MandationStatus, businessName: String): User[AnyContentAsEmpty.type] = {
    User[AnyContentAsEmpty.type]("999943620")(request.withSession(
      SessionKeys.inflightMandationStatus -> inflightMandation.toString,
      SessionKeys.mandationStatus -> mandationStatus.value
      ))
  }

  lazy val user: User[AnyContentAsEmpty.type] = User[AnyContentAsEmpty.type]("666555666", active = true)(request)

  "The OptOutPredicate" when {

    "The user has a session containing the required opt-out keys" when {

      s"The mandationStatus is not $NonMTDfB and the inflightMandationStatus is set to `false`" should {
        lazy val result = await(
          mockOptOutPredicate.refine(userWithSession(inflightMandation = false, MTDfBVoluntary, "Harold Crow Ltd"))
        ).right.get


        "return the user to continue the journey" in {
          result shouldBe userWithSession(inflightMandation = false, MTDfBVoluntary, "Harold Crow Ltd")
        }

        "not call the VatSubscriptionService" in {
          verify(mockVatSubscriptionService, never()).getCustomerInfo(any[String])(any(), any())
        }
      }

      "The inflightMandationStatus is set to `true`" should {
        lazy val result = await(
          mockOptOutPredicate.refine(userWithSession(inflightMandation = true, MTDfBMandated, "Harold Finch Ltd"))
        ).left.get
        lazy val document = Jsoup.parse(bodyOf(result))

        "return 500" in {
          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        }

        "show the generic error page" in {
          document.title shouldBe "There is a problem with the service - VAT reporting through software - GOV.UK"
        }

        "not call the VatSubscriptionService" in {
          verify(mockVatSubscriptionService, never()).getCustomerInfo(any())(any(), any())
        }
      }

      s"The mandationStatus is set to $NonMTDfB" should {
        lazy val result = await(
          mockOptOutPredicate.refine(userWithSession(inflightMandation = false, NonMTDfB, "Harold Wren Ltd"))
        ).left.get
        lazy val document = Jsoup.parse(bodyOf(result))

        "return 200" in {
          status(result) shouldBe Status.OK
        }

        "show the already opted out page" in {
          document.title shouldBe "You have already opted out of Making Tax Digital for VAT"
        }

        "not call the VatSubscriptionService" in {
          verify(mockVatSubscriptionService, never()).getCustomerInfo(any())(any(), any())
        }
      }
    }

    "The user does not have a session containing the opt-out keys" when {

      s"The mandationStatus is not $NonMTDfB and the inflightMandationStatus is set to `false`" should {
        lazy val result = {
          setup(Right(CustomerInformation(MTDfBVoluntary, inflightMandationStatus = false)))
          await(mockOptOutPredicate.refine(user)).left.get
        }

        "redirect to the start of the journey" in {
          status(result) shouldBe Status.SEE_OTHER
          result.header.headers.get("Location") shouldBe Some("/vat-through-software/account/opt-out")
        }

        "call the VatSubscriptionService" in {
          verify(mockVatSubscriptionService)
            .getCustomerInfo(any[String])(any(), any())
        }

        "send an audit event" in {
          verify(mockAuditService).audit(any(), any())(any(), any())
        }
      }

      "The inflightMandationStatus is set to `true`" should {
        lazy val result = {
          setup(Right(CustomerInformation(MTDfBVoluntary, inflightMandationStatus = true)))
          await(mockOptOutPredicate.refine(user)).left.get
        }
        lazy val document = Jsoup.parse(bodyOf(result))

        "return 500" in {
          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        }

        "show the generic error page" in {
          document.title shouldBe "There is a problem with the service - VAT reporting through software - GOV.UK"
        }

        "call the VatSubscriptionService" in {
          verify(mockVatSubscriptionService).getCustomerInfo(any())(any(), any())
        }

        "send an audit event" in {
          verify(mockAuditService).audit(any(), any())(any(), any())
        }
      }

      s"The mandationStatus is set to $NonMTDfB" should {
        lazy val result = {
          setup(Right(CustomerInformation(NonMTDfB, inflightMandationStatus = false)))
          await(mockOptOutPredicate.refine(user)).left.get
        }
        lazy val document = Jsoup.parse(bodyOf(result))

        "return 200" in {
          status(result) shouldBe Status.OK
        }

        "show the already opted out page" in {
          document.title shouldBe "You have already opted out of Making Tax Digital for VAT"
        }

        "call the VatSubscriptionService" in {
          verify(mockVatSubscriptionService).getCustomerInfo(any())(any(), any())
        }

        "send an audit event" in {
          verify(mockAuditService).audit(any(), any())(any(), any())
        }
      }

      "The call for customer info fails" should {

        lazy val result = {
          setup(Left(ErrorModel(Status.BAD_REQUEST, "Error")))
          await(mockOptOutPredicate.refine(user)).left.get
        }
        lazy val document = Jsoup.parse(bodyOf(result))

        "return 500" in {
          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        }

        "show the generic error page" in {
          document.title shouldBe "There is a problem with the service - VAT reporting through software - GOV.UK"
        }
      }
    }
  }
}
