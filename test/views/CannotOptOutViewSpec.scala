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

package views

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class CannotOptOutViewSpec extends ViewBaseSpec {

  "The cannot opt-out page for a client" should {

    lazy val view = views.html.cannotOptOut(isAgent = false)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct title" in {
      document.title shouldBe "The business cannot opt out of Making Tax Digital for VAT"
    }

    "have a heading" which {

      "has the correct text" in {
        elementText("h1") shouldBe "The business cannot opt out of Making Tax Digital for VAT"
      }

      "has the correct GA tag" in {
        element("h1").attr("data-journey") shouldBe "opt-out:threshold-error:opt-out"
      }
    }

    "have the correct paragraph" in {
      elementText("#content > article > p") shouldBe
        "This is because the business’s taxable turnover is, or has been, over the VAT threshold."
    }

    "have a button" which {

      "has the correct text" in {
        elementText(".button") shouldBe "Return to change of business details"
      }

      "has the correct href" in {
        element(".button").attr("href") shouldBe appConfig.manageVatSubscriptionServicePath
      }

      "has the correct GA tag" in {
        element(".button").attr("data-journey-click") shouldBe "opt-out:overview:opt-out"
      }
    }

    "have a back link" which {

      "has the correct text" in {
        elementText(".link-back") shouldBe "Back"
      }

      "has the correct href" in {
        element(".link-back").attr("href") shouldBe controllers.routes.TurnoverThresholdController.show().url
      }
    }
  }

  "The cannot opt-out page for an agent" should {

    lazy val view = views.html.cannotOptOut(isAgent = true)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct GA tag on the heading" in {
      element("h1").attr("data-journey") shouldBe "agent_opt-out:threshold-error:opt-out"
    }

    "have the correct GA tag on the button" in {
      element(".button").attr("data-journey-click") shouldBe "agent_opt-out:overview:opt-out"
    }
  }
}
