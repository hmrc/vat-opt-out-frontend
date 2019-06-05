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
import org.mockito.Mockito.{never, verify, when}
import play.api.http.Status
import play.api.mvc.AnyContentAsEmpty
import org.mockito.Mockito.reset
import uk.gov.hmrc.http.HeaderCarrier
import utils.MockAuth

import scala.concurrent.{ExecutionContext, Future}

class OptOutPredicateSpec extends MockAuth {

  def setup(result: GetVatSubscriptionResponse): Unit = {
    reset(mockVatSubscriptionService)
    when(mockVatSubscriptionService.getCustomerInfo(any())(any(), any()))
      .thenReturn(Future.successful(result))
  }

  val optOutPredicate = new OptOutPredicate(
    mockVatSubscriptionService,
    mockErrorHandler,
    messagesApi,
    appConfig,
    ec
  )

  def userWithSession(inflightMandation: Boolean, mandationStatus: MandationStatus): User[AnyContentAsEmpty.type] = {
    User[AnyContentAsEmpty.type]("999943620")(request.withSession(
      SessionKeys.inflightMandationStatus -> inflightMandation.toString,
      SessionKeys.mandationStatus -> mandationStatus.value
      ))
  }

  lazy val user: User[AnyContentAsEmpty.type] = User[AnyContentAsEmpty.type]("666555666", active = true)(request)


  "The OptOutPredicate" when {

    "The user has a session containing the required opt-out keys" when {

      s"The mandationStatus is not $NonMTDfB and the inflightMandationStatus is set to `false`" should {
        lazy val result = await(optOutPredicate.refine(userWithSession(false, MTDfBVoluntary))).right.get


        "return the user to continue the journey" in {
          result shouldBe userWithSession(false,MTDfBVoluntary)
        }

        "not call the VatSubscriptionService" in {
          verify(mockVatSubscriptionService, never())
            .getCustomerInfo(any[String])(any[HeaderCarrier], any[ExecutionContext])
        }
      }

      "The inflightMandationStatus is set to `true`" should {
        lazy val result = await(optOutPredicate.refine(userWithSession(true, MTDfBMandated))).left.get
        lazy val document = Jsoup.parse(bodyOf(result))

        "return 500" in {
          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        }

        "show the generic error page" in {
          document.title shouldBe "Sorry, we are experiencing technical difficulties - 500"
        }

        "not call the VatSubscriptionService" in {
          verify(mockVatSubscriptionService, never())
            .getCustomerInfo(any[String])(any[HeaderCarrier], any[ExecutionContext])
        }
      }

      s"The mandationStatus is set to $NonMTDfB" should {
        lazy val result = await(optOutPredicate.refine(userWithSession(false, NonMTDfB))).left.get
        lazy val document = Jsoup.parse(bodyOf(result))

        "return 200" in {
          status(result) shouldBe Status.OK
        }

        "show the already opted out page" in {
          document.title shouldBe "You have already opted out of Making Tax Digital for VAT"
        }

        "not call the VatSubscriptionService" in {
          verify(mockVatSubscriptionService, never())
            .getCustomerInfo(any[String])(any[HeaderCarrier], any[ExecutionContext])
        }
      }
    }

    "The user does not have a session containing the opt-out keys" when {

      s"The mandationStatus is not $NonMTDfB and the inflightMandationStatus is set to `false`" should {
        lazy val result = {
          setup(Right(CustomerInformation(MTDfBVoluntary, false)))
          await(optOutPredicate.refine(user)).left.get
        }

        "redirect to the start of the journey" in {
          status(result) shouldBe Status.SEE_OTHER
          result.header.headers.get("Location") shouldBe Some("/vat-through-software/account/opt-out")
        }

        "call the VatSubscriptionService" in {
          verify(mockVatSubscriptionService)
            .getCustomerInfo(any[String])(any[HeaderCarrier], any[ExecutionContext])
        }
      }

      "The inflightMandationStatus is set to `true`" should {
        lazy val result = {
          setup(Right(CustomerInformation(MTDfBVoluntary, true)))
          await(optOutPredicate.refine(user)).left.get
        }
        lazy val document = Jsoup.parse(bodyOf(result))

        "return 500" in {
          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        }

        "show the generic error page" in {
          document.title shouldBe "Sorry, we are experiencing technical difficulties - 500"
        }

        "call the VatSubscriptionService" in {
          verify(mockVatSubscriptionService)
            .getCustomerInfo(any[String])(any[HeaderCarrier], any[ExecutionContext])
        }
      }

      s"The mandationStatus is set to $NonMTDfB" should {
        lazy val result = {
          setup(Right(CustomerInformation(NonMTDfB, false)))
          await(optOutPredicate.refine(user)).left.get
        }
        lazy val document = Jsoup.parse(bodyOf(result))

        "return 200" in {
          status(result) shouldBe Status.OK
        }

        "show the already opted out page" in {
          document.title shouldBe "You have already opted out of Making Tax Digital for VAT"
        }

        "call the VatSubscriptionService" in {
          verify(mockVatSubscriptionService)
            .getCustomerInfo(any[String])(any[HeaderCarrier], any[ExecutionContext])
        }
      }

      "The call for customer info fails" should {
        lazy val result = {
          setup(Left(ErrorModel(400, "Error")))
          await(optOutPredicate.refine(user)).left.get
        }
        lazy val document = Jsoup.parse(bodyOf(result))
        "return 500" in {
          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        }
        "show the generic error page" in {
          document.title shouldBe "Sorry, we are experiencing technical difficulties - 500"
        }
      }
    }
  }
}