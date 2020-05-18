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

package views

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.html.CannotOptOutView

class CannotOptOutViewSpec extends ViewBaseSpec {

  val injectedView: CannotOptOutView = injector.instanceOf[CannotOptOutView]

  "The cannot opt-out page for a client" should {
    lazy val view = injectedView()(request, messages, appConfig, clientUser)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct title" in {
      document.title shouldBe "The business cannot opt out of Making Tax Digital for VAT - Business tax account - GOV.UK"
    }

    "have a heading" which {

      "has the correct text" in {
        elementText("h1") shouldBe "The business cannot opt out of Making Tax Digital for VAT"
      }

    }

    "have the correct paragraph" in {
      elementText("#content > p") shouldBe
        "This is because the business’s taxable turnover is, or has been, over the VAT threshold."
    }

    "have a button" which {

      "has the correct text" in {
        elementText(".govuk-button") shouldBe "Return to your VAT account"
      }

      "has the correct href" in {
        element(".govuk-button").attr("href") shouldBe appConfig.vatSummaryServicePath
      }
    }

    "have a back link" which {

      "has the correct text" in {
        elementText(".govuk-back-link") shouldBe "Back"
      }

      "has the correct href" in {
        element(".govuk-back-link").attr("href") shouldBe controllers.routes.TurnoverThresholdController.show().url
      }
    }
  }

  "The cannot opt-out page for an agent" should {

    lazy val view = injectedView()(request, messages, appConfig, agentUser)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe "The business cannot opt out of Making Tax Digital for VAT - Your client’s VAT details - GOV.UK"
    }

    "have a button" which {

      "has the correct text" in {
        elementText(".govuk-button") shouldBe "View your client options"
      }

      "has the correct href" in {
        element(".govuk-button").attr("href") shouldBe appConfig.agentClientLookupChoicesPath
      }
    }
  }
}
