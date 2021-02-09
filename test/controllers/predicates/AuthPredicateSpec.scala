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

package controllers.predicates

import common.SessionKeys
import connectors.httpParsers.GetVatSubscriptionHttpParser.GetVatSubscriptionResponse
import models.{CustomerInformation, ErrorModel, MTDfBMandated}
import org.jsoup.Jsoup
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import play.api.http.Status
import play.api.mvc.Results.Ok
import play.api.mvc.{Action, AnyContent}
import play.api.test.Helpers._
import utils.MockAuth

import scala.concurrent.Future

class AuthPredicateSpec extends MockAuth {

  def setup(result: GetVatSubscriptionResponse): Unit = {
    reset(mockVatSubscriptionService)
    reset(mockAuditService)
    when(mockVatSubscriptionService.getCustomerInfo(any())(any(), any()))
      .thenReturn(Future.successful(result))
  }

  val target: Action[AnyContent] = mockAuthPredicate.async {
    _ => Future.successful(Ok("test"))
  }

  "AuthPredicate" when {

    "the user is an Agent" when {

      "the Agent has an Active HMRC-AS-AGENT enrolment" when {

        "a successful authorisation result is returned from Auth" should {

          "return OK (200)" in {
            mockAgentAuthorised()
            status(target(requestWithClientVRN)) shouldBe Status.OK
          }
        }

        "a no active session result is returned from Auth" should {

          lazy val result = await(target(agentUserWithClient))

          "return Unauthorised (401)" in {
            mockMissingBearerToken()
            status(result) shouldBe Status.UNAUTHORIZED
          }

          "render the Session Timeout page" in {
            messages(Jsoup.parse(bodyOf(result)).select("h1").text) shouldBe "Your session has timed out"
          }
        }

        "an authorization exception is returned from Auth" should {

          lazy val result = await(target(agentUserWithClient))

          "return Internal Server Error (500)" in {
            mockUnauthorised()
            status(result) shouldBe Status.INTERNAL_SERVER_ERROR
          }

          "render the Standard Error page" in {
            messages(Jsoup.parse(bodyOf(result)).select("h1").text) shouldBe "Sorry, there is a problem with the service"
          }
        }
      }

      "the Agent does NOT have an Active HMRC-AS-AGENT enrolment" should {

        lazy val result = await(target(agentUserWithClient))

        "return Forbidden (403)" in {
          mockAgentWithoutEnrolment()
          status(result) shouldBe Status.FORBIDDEN
        }

        "render the Standard Error page" in {
          messages(Jsoup.parse(bodyOf(result)).select("h1").text) shouldBe "You can’t use this service yet"
        }
      }
    }

    "the user is an Individual (Principle Entity)" when {

      "they have an active HMRC-MTD-VAT enrolment" when {

        "they have a session value for their insolvency status" when {

          "the user is not insolvent and is continuing to trade (status false)" should {

            "return OK (200)" in {
              mockIndividualAuthorised()
              status(target(request)) shouldBe Status.OK
            }
          }

          "the user is insolvent and not continuing to trade (status true)" should {

            lazy val result = await(target(requestInsolvent))

            "return Forbidden (403)" in {
              mockIndividualAuthorised()
              status(result) shouldBe Status.FORBIDDEN
            }

            "render the Standard Error page" in {
              messages(Jsoup.parse(bodyOf(result)).select("h1").text) shouldBe "You are not authorised to use this service"
          }

        }

        "they do not have a insolvency status value in session" when {

          "they are insolvent and not continuing to trade" should {

            lazy val result = {
              mockIndividualAuthorised()
              setup(Right(CustomerInformation(MTDfBMandated, isInsolvent = true,
                continueToTrade = Some(false), inflightMandationStatus = false, None)))
              await(target(requestNoInsolventSessionKey))
            }

            "return Forbidden (403)" in {
              status(result) shouldBe Status.FORBIDDEN
            }

            "add the insolvent flag to the session" in {
              session(result).get(SessionKeys.insolventWithoutAccessKey) shouldBe Some("true")
            }

            "render the Standard Error page" in {
              messages(Jsoup.parse(bodyOf(result)).select("h1").text) shouldBe "You are not authorised to use this service"
            }
          }

          "they are permitted to trade" should {

            lazy val result = {
              mockIndividualAuthorised()
              setup(Right(CustomerInformation(MTDfBMandated, isInsolvent = false,
                continueToTrade = Some(true), inflightMandationStatus = false, None)))
              await(target(requestNoInsolventSessionKey))
            }

            "return OK (200)" in {
              status(result) shouldBe Status.OK
            }

            "add the insolvent flag to the session" in {
              session(result).get(SessionKeys.insolventWithoutAccessKey) shouldBe Some("false")
            }
          }

          "there is an error returned from the customer information API" should {

            lazy val result = {
              mockUnauthorised()
              setup(Left(ErrorModel(Status.BAD_REQUEST, "Error")))
              await(target(requestNoInsolventSessionKey))
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

      "they do NOT have an active HMRC-MTD-VAT enrolment" should {

        lazy val result = await(target(clientUser))

        "return Forbidden (403)" in {
          mockIndividualWithoutEnrolment()
          status(result) shouldBe Status.FORBIDDEN
        }

        "render the Unauthorised page" in {
          messages(Jsoup.parse(bodyOf(result)).select("h1").text) shouldBe "You are not authorised to use this service"
        }
      }

      "the user does not have affinity group" should {

        lazy val result = await(target(clientUser))

        "return Internal Server Error (500)" in {
          mockUserWithoutAffinity()
          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        }

        "render the Standard Error page" in {
          messages(Jsoup.parse(bodyOf(result)).select("h1").text) shouldBe "Sorry, there is a problem with the service"
        }
      }
    }
  }
}
