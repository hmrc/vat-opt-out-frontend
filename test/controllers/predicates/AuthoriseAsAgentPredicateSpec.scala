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
import play.api.mvc.{Action, AnyContent}
import play.api.mvc.Results.Ok
import utils.MockAuth

import scala.concurrent.Future

class AuthoriseAsAgentPredicateSpec extends MockAuth {

  def target: Action[AnyContent] = mockAuthAsAgentWithClient.async {
    implicit user => Future.successful(Ok("Test"))
  }

  "AuthoriseAsAgentWithClient" when {

    "the agent is authorised with a Client VRN in session" should {

      "return OK (200)" in {
        mockAgentAuthorised()
        val result = target(requestWithClientVRN)
        status(result) shouldBe Status.OK
      }
    }

    "the agent is not authenticated" should {

      lazy val result = target(requestWithClientVRN)

      "return Unauthorised (401)" in {
        mockMissingBearerToken()
        status(result) shouldBe Status.UNAUTHORIZED
      }

      "render the Session Timeout page" in {
        Jsoup.parse(bodyOf(result)).title shouldBe "Your session has timed out"
      }
    }

    "the agent is not authorised" should {

      lazy val result = target(requestWithClientVRN)

      "return Internal Server Error (500)" in {
        mockUnauthorised()
        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      }

      "render the Standard Error page" in {
        Jsoup.parse(bodyOf(result)).title shouldBe "Sorry, we are experiencing technical difficulties - 500"
      }

    }

    "the agent has no enrolments" should {

      lazy val result = await(target(requestWithClientVRN))

      "return Internal Server Error (500)" in {
        mockAgentWithoutAffinity()
        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      }

      "render the Internal Server Error page" in {
        Jsoup.parse(bodyOf(result)).title shouldBe "Sorry, we are experiencing technical difficulties - 500"
      }
    }

    "there is no client VRN in session" should {

      lazy val result = await(target(request))

      "return Internal Server Error (500)" in {
        mockAgentAuthorised()
        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      }

      "render the Internal Server Error page" in {
        Jsoup.parse(bodyOf(result)).title shouldBe "Sorry, we are experiencing technical difficulties - 500"
      }
    }
  }
}
