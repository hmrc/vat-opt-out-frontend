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

package views.errors

import common.EnrolmentKeys
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import uk.gov.hmrc.play.binders.ContinueUrl
import views.ViewBaseSpec

class UnauthorisedForClientViewSpec extends ViewBaseSpec {

  "The unauthorised for client page" should {

    lazy val view = views.html.errors.unauthorisedForClient("123456789")
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe "You’re not authorised for this client - VAT - GOV.UK"
    }

    "have a the correct page heading" in {
      elementText("h1") shouldBe "You’re not authorised for this client"
    }

    "have the correct instructions in the form on the page" in {
      elementText("#content form > p") shouldBe "To use this service, your client needs to authorise you as their agent."
    }

    "have a form" which {

      "has a POST action to the agent fast track service" in {
        val form = element("#agentInviteForm")
        form.attr("method") shouldBe "POST"
        form.attr("action") shouldBe
          s"${appConfig.agentInvitationsFastTrack}?continue=${ContinueUrl(appConfig.vatOptOutServicePath).encodedUrl}"
      }

      "has a hidden field for the VAT service enrolment id" in {
        val input = element("#agentInviteForm input[name=service]")
        input.attr("value") shouldBe EnrolmentKeys.vatEnrolmentId
        input.attr("type") shouldBe "hidden"
      }

      "has a hidden field for the VAT service identifier id" in {
        val input = element("#agentInviteForm input[name=clientIdentifierType]")
        input.attr("value") shouldBe EnrolmentKeys.vatIdentifierId.toLowerCase
        input.attr("type") shouldBe "hidden"
      }

      "has a hidden field for the VRN" in {
        val input = element("#agentInviteForm input[name=clientIdentifier]")
        input.attr("value") shouldBe "123456789"
        input.attr("type") shouldBe "hidden"
      }

      "has a link which submits the form" in {
        val input = element("#content form > p > a")
        input.attr("onClick") shouldBe "document.getElementById('agentInviteForm').submit();"
      }
    }

    "have the correct content for trying again" in {
      elementText("#content article > p") shouldBe "If you think you have entered the wrong details you can try again."
    }

    "have a link to the agent client service" which {

      "has the correct text" in {
        elementText("#content article > p > a") shouldBe "try again"
      }

      "has the correct href" in {
        element("#content article > p > a").attr("href") shouldBe appConfig.agentClientLookupHandoff
      }
    }

    "have a sign out button" which {

      "has the correct text" in {
        elementText(".button") shouldBe "Sign out"
      }

      "have the correct href" in {
        element(".button").attr("href") shouldBe controllers.routes.SignOutController.signOut(authorised = true).url
      }
    }
  }
}
