/*
 * Copyright 2020 HM Revenue & Customs
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
import play.api.test.Helpers._
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

  def userWithSession(inflightMandation: Boolean, mandationStatus: MandationStatus): User[AnyContentAsEmpty.type] = {
    User[AnyContentAsEmpty.type]("999943620")(request.withSession(
      SessionKeys.inflightMandationStatus -> inflightMandation.toString,
      SessionKeys.mandationStatus -> mandationStatus.value
    ))
  }

  def agentWithSession(inflightMandation: Boolean, mandationStatus: MandationStatus): User[AnyContentAsEmpty.type] = {
    User[AnyContentAsEmpty.type]("999943620", arn = Some("XARN1234567"))(request.withSession(
      SessionKeys.clientVrn -> "999943620",
      SessionKeys.inflightMandationStatus -> inflightMandation.toString,
      SessionKeys.mandationStatus -> mandationStatus.value
    ))
  }

  "The OptOutPredicate" when {

    "The user has a session containing the required opt-out keys" when {

      s"The mandationStatus is not $NonMTDfB and not $NonDigital" when {

        "the inflightMandationStatus is `false`" should {

          lazy val result =
            await(mockOptOutPredicate.refine(userWithSession(inflightMandation = false, MTDfBVoluntary))).right.get


          "return the user to continue the journey" in {
            result shouldBe userWithSession(inflightMandation = false, MTDfBVoluntary)
          }

          "not call the VatSubscriptionService" in {
            verify(mockVatSubscriptionService, never()).getCustomerInfo(any[String])(any(), any())
          }
        }

        "The inflightMandationStatus is `true`" when {

          "the user is an agent" should {

            lazy val result =
              await(mockOptOutPredicate.refine(agentWithSession(inflightMandation = true, MTDfBMandated))).left.get

            "return 303" in {
              status(result) shouldBe Status.SEE_OTHER
            }

            "redirect the user to the client choices page" in {
              redirectLocation(result) shouldBe Some(appConfig.agentClientHubPath)
            }

            "not call the VatSubscriptionService" in {
              verify(mockVatSubscriptionService, never()).getCustomerInfo(any())(any(), any())
            }
          }

          "the user is an individual or organisation" should {

            lazy val result =
              await(mockOptOutPredicate.refine(userWithSession(inflightMandation = true, MTDfBMandated))).left.get

            "return 303" in {
              status(result) shouldBe Status.SEE_OTHER
            }

            "redirect the user to the VAT overview page" in {
              redirectLocation(result) shouldBe Some(appConfig.vatSummaryServicePath)
            }

            "not call the VatSubscriptionService" in {
              verify(mockVatSubscriptionService, never()).getCustomerInfo(any())(any(), any())
            }
          }
        }
      }

      s"The mandationStatus is $NonMTDfB" when {

        "the user is an agent" should {

          lazy val result =
            await(mockOptOutPredicate.refine(agentWithSession(inflightMandation = false, NonMTDfB))).left.get

          "return 303" in {
            status(result) shouldBe Status.SEE_OTHER
          }

          "redirect the user to the client choices page" in {
            redirectLocation(result) shouldBe Some(appConfig.agentClientHubPath)
          }

          "not call the VatSubscriptionService" in {
            verify(mockVatSubscriptionService, never()).getCustomerInfo(any())(any(), any())
          }
        }

        "the user is an individual or organisation" should {

          lazy val result =
            await(mockOptOutPredicate.refine(userWithSession(inflightMandation = false, NonMTDfB))).left.get

          "return 303" in {
            status(result) shouldBe Status.SEE_OTHER
          }

          "redirect the user to the VAT overview page" in {
            redirectLocation(result) shouldBe Some(appConfig.vatSummaryServicePath)
          }

          "not call the VatSubscriptionService" in {
            verify(mockVatSubscriptionService, never()).getCustomerInfo(any())(any(), any())
          }
        }
      }

      s"The mandationStatus is $NonDigital" when {

        "the user is an agent" should {

          lazy val result =
            await(mockOptOutPredicate.refine(agentWithSession(inflightMandation = false, NonDigital))).left.get

          "return 303" in {
            status(result) shouldBe Status.SEE_OTHER
          }

          "redirect the user to the client choices page" in {
            redirectLocation(result) shouldBe Some(appConfig.agentClientHubPath)
          }

          "not call the VatSubscriptionService" in {
            verify(mockVatSubscriptionService, never()).getCustomerInfo(any())(any(), any())
          }
        }

        "the user is an individual or organisation" should {

          lazy val result =
            await(mockOptOutPredicate.refine(userWithSession(inflightMandation = false, NonDigital))).left.get

          "return 303" in {
            status(result) shouldBe Status.SEE_OTHER
          }

          "redirect the user to the VAT overview page" in {
            redirectLocation(result) shouldBe Some(appConfig.vatSummaryServicePath)
          }

          "not call the VatSubscriptionService" in {
            verify(mockVatSubscriptionService, never()).getCustomerInfo(any())(any(), any())
          }
        }
      }

      s"The mandationStatus is $MTDfBExempt" when {

        "the user is an agent" should {

          lazy val result =
            await(mockOptOutPredicate.refine(agentWithSession(inflightMandation = false, MTDfBExempt))).left.get

          "return 303" in {
            status(result) shouldBe Status.SEE_OTHER
          }

          "redirect the user to the client choices page" in {
            redirectLocation(result) shouldBe Some(appConfig.agentClientHubPath)
          }

          "not call the VatSubscriptionService" in {
            verify(mockVatSubscriptionService, never()).getCustomerInfo(any())(any(), any())
          }
        }

        "the user is an individual or organisation" should {

          lazy val result =
            await(mockOptOutPredicate.refine(userWithSession(inflightMandation = false, MTDfBExempt))).left.get

          "return 303" in {
            status(result) shouldBe Status.SEE_OTHER
          }

          "redirect the user to the VAT overview page" in {
            redirectLocation(result) shouldBe Some(appConfig.vatSummaryServicePath)
          }

          "not call the VatSubscriptionService" in {
            verify(mockVatSubscriptionService, never()).getCustomerInfo(any())(any(), any())
          }
        }
      }
    }

    "The user does not have a session containing the opt-out keys" when {

      s"The mandationStatus is not $NonMTDfB and not $NonDigital" when {

        "the inflightMandationStatus is `false`" should {

          lazy val result = {
            setup(Right(CustomerInformation(MTDfBVoluntary, inflightMandationStatus = false)))
            await(mockOptOutPredicate.refine(clientUser)).left.get
          }

          "redirect to the start of the journey" in {
            status(result) shouldBe Status.SEE_OTHER
            result.header.headers.get("Location") shouldBe Some("/vat-through-software/account/opt-out")
          }

          "call the VatSubscriptionService" in {
            verify(mockVatSubscriptionService).getCustomerInfo(any[String])(any(), any())
          }

          "send an audit event" in {
            verify(mockAuditService).audit(any(), any())(any(), any())
          }
        }

        "The inflightMandationStatus is `true`" should {

          "the user is an agent" should {

            lazy val result = {
              setup(Right(CustomerInformation(MTDfBVoluntary, inflightMandationStatus = true)))
              await(mockOptOutPredicate.refine(agentUser)).left.get
            }

            "return 303" in {
              status(result) shouldBe Status.SEE_OTHER
            }

            "redirect the user to the client choices page" in {
              redirectLocation(result) shouldBe Some(appConfig.agentClientHubPath)
            }

            "call the VatSubscriptionService" in {
              verify(mockVatSubscriptionService).getCustomerInfo(any())(any(), any())
            }

            "send an audit event" in {
              verify(mockAuditService).audit(any(), any())(any(), any())
            }
          }

          "the user is an individual or organisation" should {

            lazy val result = {
              setup(Right(CustomerInformation(MTDfBVoluntary, inflightMandationStatus = true)))
              await(mockOptOutPredicate.refine(clientUser)).left.get
            }

            "return 303" in {
              status(result) shouldBe Status.SEE_OTHER
            }

            "redirect the user to the VAT overview page" in {
              redirectLocation(result) shouldBe Some(appConfig.vatSummaryServicePath)
            }

            "call the VatSubscriptionService" in {
              verify(mockVatSubscriptionService).getCustomerInfo(any())(any(), any())
            }

            "send an audit event" in {
              verify(mockAuditService).audit(any(), any())(any(), any())
            }
          }
        }
      }

      s"The mandationStatus is $NonMTDfB" when {

        "the user is an agent" should {

          lazy val result = {
            setup(Right(CustomerInformation(NonMTDfB, inflightMandationStatus = false)))
            await(mockOptOutPredicate.refine(agentUser)).left.get
          }

          "return 303" in {
            status(result) shouldBe Status.SEE_OTHER
          }

          "redirect the user to the client choices page" in {
            redirectLocation(result) shouldBe Some(appConfig.agentClientHubPath)
          }

          "call the VatSubscriptionService" in {
            verify(mockVatSubscriptionService).getCustomerInfo(any())(any(), any())
          }

          "send an audit event" in {
            verify(mockAuditService).audit(any(), any())(any(), any())
          }

          s"add the value of $NonMTDfB to the session" in {
            session(result).get(SessionKeys.mandationStatus) shouldBe Some(NonMTDfB.value)
          }
        }

        "the user is an individual or organisation" should {

          lazy val result = {
            setup(Right(CustomerInformation(NonMTDfB, inflightMandationStatus = false)))
            await(mockOptOutPredicate.refine(clientUser)).left.get
          }

          "return 303" in {
            status(result) shouldBe Status.SEE_OTHER
          }

          "redirect the user to the VAT overview page" in {
            redirectLocation(result) shouldBe Some(appConfig.vatSummaryServicePath)
          }

          "call the VatSubscriptionService" in {
            verify(mockVatSubscriptionService).getCustomerInfo(any())(any(), any())
          }

          "send an audit event" in {
            verify(mockAuditService).audit(any(), any())(any(), any())
          }

          s"add the value of $NonMTDfB to the session" in {
            session(result).get(SessionKeys.mandationStatus) shouldBe Some(NonMTDfB.value)
          }
        }
      }

      s"The mandationStatus is $NonDigital" when {

        "the user is an agent" should {

          lazy val result = {
            setup(Right(CustomerInformation(NonDigital, inflightMandationStatus = false)))
            await(mockOptOutPredicate.refine(agentUser)).left.get
          }

          "return 303" in {
            status(result) shouldBe Status.SEE_OTHER
          }

          "redirect the user to the client choices page" in {
            redirectLocation(result) shouldBe Some(appConfig.agentClientHubPath)
          }

          "call the VatSubscriptionService" in {
            verify(mockVatSubscriptionService).getCustomerInfo(any())(any(), any())
          }

          "send an audit event" in {
            verify(mockAuditService).audit(any(), any())(any(), any())
          }

          s"add the value of $NonDigital to the session" in {
            session(result).get(SessionKeys.mandationStatus) shouldBe Some(NonDigital.value)
          }
        }

        "the user is an individual or organisation" should {

          lazy val result = {
            setup(Right(CustomerInformation(NonDigital, inflightMandationStatus = false)))
            await(mockOptOutPredicate.refine(clientUser)).left.get
          }

          "return 303" in {
            status(result) shouldBe Status.SEE_OTHER
          }

          "redirect the user to the VAT overview page" in {
            redirectLocation(result) shouldBe Some(appConfig.vatSummaryServicePath)
          }

          "call the VatSubscriptionService" in {
            verify(mockVatSubscriptionService).getCustomerInfo(any())(any(), any())
          }

          "send an audit event" in {
            verify(mockAuditService).audit(any(), any())(any(), any())
          }

          s"add the value of $NonDigital to the session" in {
            session(result).get(SessionKeys.mandationStatus) shouldBe Some(NonDigital.value)
          }
        }
      }

      s"The mandationStatus is $MTDfBExempt" when {

        "the user is an agent" should {

          lazy val result = {
            setup(Right(CustomerInformation(MTDfBExempt, inflightMandationStatus = false)))
            await(mockOptOutPredicate.refine(agentUser)).left.get
          }

          "return 303" in {
            status(result) shouldBe Status.SEE_OTHER
          }

          "redirect the user to the client choices page" in {
            redirectLocation(result) shouldBe Some(appConfig.agentClientHubPath)
          }

          "call the VatSubscriptionService" in {
            verify(mockVatSubscriptionService).getCustomerInfo(any())(any(), any())
          }

          "send an audit event" in {
            verify(mockAuditService).audit(any(), any())(any(), any())
          }

          s"add the value of $MTDfBExempt to the session" in {
            session(result).get(SessionKeys.mandationStatus) shouldBe Some(MTDfBExempt.value)
          }
        }

        "the user is an individual or organisation" should {

          lazy val result = {
            setup(Right(CustomerInformation(MTDfBExempt, inflightMandationStatus = false)))
            await(mockOptOutPredicate.refine(clientUser)).left.get
          }

          "return 303" in {
            status(result) shouldBe Status.SEE_OTHER
          }

          "redirect the user to the VAT overview page" in {
            redirectLocation(result) shouldBe Some(appConfig.vatSummaryServicePath)
          }

          "call the VatSubscriptionService" in {
            verify(mockVatSubscriptionService).getCustomerInfo(any())(any(), any())
          }

          "send an audit event" in {
            verify(mockAuditService).audit(any(), any())(any(), any())
          }

          s"add the value of $MTDfBExempt to the session" in {
            session(result).get(SessionKeys.mandationStatus) shouldBe Some(MTDfBExempt.value)
          }
        }
      }

      "The call for customer info fails" should {

          lazy val result = {
            setup(Left(ErrorModel(Status.BAD_REQUEST, "Error")))
            await(mockOptOutPredicate.refine(clientUser)).left.get
          }
          lazy val document = Jsoup.parse(bodyOf(result))

          "return 500" in {
            status(result) shouldBe Status.INTERNAL_SERVER_ERROR
          }

          "show the generic error page" in {
            document.title shouldBe "There is a problem with the service - VAT - GOV.UK"
          }
        }
    }
  }
}
