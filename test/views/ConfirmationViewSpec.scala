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

package views

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.html.ConfirmationView

class ConfirmationViewSpec extends ViewBaseSpec {

  val injectedView: ConfirmationView = injector.instanceOf[ConfirmationView]

  "The confirmation page for a client" should {

    lazy val view = injectedView()(messages, appConfig, clientUser)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct title" in {
      document.title shouldBe "You have opted out of Making Tax Digital for VAT - Business tax account - GOV.UK"
    }

    "have the correct heaading" in {
      elementText("h1") shouldBe "You have opted out of Making Tax Digital for VAT"
    }

    "have the correct subheading" in {
      elementText("h2") shouldBe "What happens next"
    }

    "have the correct first paragraph" in {
      elementText("#content > p:nth-of-type(1)") shouldBe "This does not cancel your VAT registration."
    }

    "have the correct second paragraph" in {
      elementText("#content > p:nth-of-type(2)") shouldBe "For your current return period, you must " +
        "continue to submit your VAT Returns using software compatible with Making Tax Digital."
    }

    "have a third paragraph" which {

      "has the correct text" in {
        elementText("#content > p:nth-of-type(3)") shouldBe
          "Future VAT Returns must be submitted using your online VAT account, " +
          "starting from your next return period. This change can take 2 days to come " +
          "into effect."
      }

      "has the correct link text for your online VAT account" in {
        elementText("#content > p:nth-of-type(3) > a") shouldBe "online VAT account"
      }

      "has the correct link location for your online VAT account" in {
        element("#content > p:nth-of-type(3) > a").attr("href") shouldBe appConfig.vatSummaryServicePath
      }
    }

    "have the correct fourth paragraph" in {
      elementText("#content > p:nth-of-type(4)") shouldBe
        "If your taxable turnover goes above £85,000, you must sign up again for Making Tax Digital."
    }


    "have a button which" should {

      "have the correct text" in {
        elementText(".govuk-button") shouldBe "Finish"
      }

      "have the correct link location" in {
        element(".govuk-button").attr("href") shouldBe appConfig.vatSummaryServicePath
      }
    }
  }

  "The confirmation page for an agent" should {

    lazy val view = injectedView()(messages, appConfig, agentUser)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct title" in {
      document.title shouldBe
        "You have opted your client out of Making Tax Digital for VAT - Your client’s VAT details - GOV.UK"
    }

    "have the correct heading" in {
      elementText("h1") shouldBe "You have opted your client out of Making Tax Digital for VAT"
    }

    "have the correct subheading" in {
      elementText("h2") shouldBe "What happens next"
    }

    "have the correct first paragraph" in {
      elementText("#content > p:nth-of-type(1)") shouldBe "This does not cancel your client’s VAT registration."
    }

    "have the correct second paragraph" in {
      elementText("#content > p:nth-of-type(2)") shouldBe "For your client’s current return period, you " +
        "must continue to submit their VAT Returns using software compatible with Making Tax Digital."
    }

    "have a third paragraph" which {

      "has the correct text" in {
        elementText("#content > p:nth-of-type(3)") shouldBe
          "Future VAT Returns must be submitted online, starting from your client’s next " +
            "return period. This change can take 2 days to come into effect."
      }

      "has the correct link text" in {
        elementText("#content > p:nth-of-type(3) > a") shouldBe "online"
      }

      "has the correct link location" in {
        element("#content > p:nth-of-type(3) > a").attr("href") shouldBe appConfig.agentClientHubPath
      }
    }

    "have the correct fourth paragraph" in {
      elementText("#content > p:nth-of-type(4)") shouldBe "If your client’s taxable turnover goes above " +
        "£85,000, you must sign them up again for Making Tax Digital."
    }

    "have a button which" should {

      "have the correct text" in {
        elementText(".govuk-button") shouldBe "Back to client’s details"
      }

      "have the correct link location" in {
        element(".govuk-button").attr("href") shouldBe appConfig.agentClientHubPath
      }
    }
  }
}
