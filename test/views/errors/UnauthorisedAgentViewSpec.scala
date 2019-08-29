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

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.ViewBaseSpec

class UnauthorisedAgentViewSpec extends ViewBaseSpec {

  "Rendering the unauthorised agent page" should {

    lazy val view = views.html.errors.unauthorisedAgent()(agentUser, messages, appConfig)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe "You can’t use this service yet - Business tax account - GOV.UK"
    }

    "have the correct page heading" in {
      elementText("#content h1") shouldBe "You can’t use this service yet"
    }

    "have the correct instructions on the page" in {
      elementText("#content > article p") shouldBe "To use this service, you need to set up an agent services account."
    }

    "have a link to set up an agent services account" which {

      "has the correct text" in {
        elementText("#content > article p > a") shouldBe "set up an agent services account"
      }

      "have the correct href" in {
        element("#content >article p > a").attr("href") shouldBe appConfig.agentServicesGovUkGuidance
      }
    }

    "have a button to sign out" which {

      "has the correct text" in {
        elementText(".button") shouldBe "Sign out"
      }

      "have the correct href" in {
        element(".button").attr("href") shouldBe controllers.routes.SignOutController.signOut(authorised = false).url
      }
    }
  }
}
