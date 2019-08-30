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

import org.jsoup.Jsoup
import play.api.http.Status
import play.api.mvc.Results.Ok
import play.api.mvc.{Action, AnyContent}
import utils.MockAuth

import scala.concurrent.Future

class AuthPredicateSpec extends MockAuth {

  val target: Action[AnyContent] = mockAuthPredicate.async {
    implicit request => Future.successful(Ok("test"))
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
            Jsoup.parse(bodyOf(result)).title shouldBe "Your session has timed out - VAT - GOV.UK"
          }
        }

        "an authorization exception is returned from Auth" should {

          lazy val result = await(target(agentUserWithClient))

          "return Internal Server Error (500)" in {
            mockUnauthorised()
            status(result) shouldBe Status.INTERNAL_SERVER_ERROR
          }

          "render the Standard Error page" in {
            Jsoup.parse(bodyOf(result)).title shouldBe "There is a problem with the service - VAT - GOV.UK"
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
          Jsoup.parse(bodyOf(result)).title shouldBe "You can’t use this service yet - VAT - GOV.UK"
        }
      }
    }

    "the user is an Individual (Principle Entity)" when {

      "they have an active HMRC-MTD-VAT enrolment" should {

        "return OK (200)" in {
          mockIndividualAuthorised()
          status(target(request)) shouldBe Status.OK
        }
      }

      "they do NOT have an active HMRC-MTD-VAT enrolment" should {

        lazy val result = await(target(clientUser))

        "return Forbidden (403)" in {
          mockIndividualWithoutEnrolment()
          status(result) shouldBe Status.FORBIDDEN
        }

        "render the Unauthorised page" in {
          Jsoup.parse(bodyOf(result)).title shouldBe "You are not authorised to use this service - VAT - GOV.UK"
        }
      }

      "the user does not have affinity group" should {

        lazy val result = await(target(clientUser))

        "return Internal Server Error (500)" in {
          mockUserWithoutAffinity()
          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        }

        "render the Standard Error page" in {
          Jsoup.parse(bodyOf(result)).title shouldBe "There is a problem with the service - VAT - GOV.UK"
        }
      }
    }
  }
}
