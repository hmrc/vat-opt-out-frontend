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

class ConfirmationViewSpec extends ViewBaseSpec {

  "The confirmation page for a client" should {

    lazy val view = views.html.confirmation(isAgent = false)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct title" in {
      document.title shouldBe "You have opted out of Making Tax Digital for VAT"
    }

    "have a heading" which {

      "has the correct text" in {
        elementText("h1") shouldBe "You have opted out of Making Tax Digital for VAT"
      }

      "has the correct GA tag" in {
        element("h1").attr("data-journey") shouldBe "opt-out:confirm:opt-out"
      }
    }

    "have the correct subheading" in {
      elementText("h2") shouldBe "What happens next"
    }

    "have the correct first paragraph" in {
      elementText("#content > article > p:nth-of-type(1)") shouldBe "You must continue to use software " +
        "compatible with Making Tax Digital to submit your VAT Returns for your current return period."
    }

    "have the correct second paragraph" in {
      elementText("#content > article > p:nth-of-type(2)") shouldBe
        "You must submit your VAT Return using your online VAT account from your next return period."
    }

    "have a third paragraph" which {

      "has the correct text" in {
        elementText("#content > article > p:nth-of-type(3)") shouldBe "If your taxable turnover goes above " +
          "£85,000, you must contact us (opens in a new tab) to sign up again for Making Tax Digital."
      }

      "has the correct link text" in {
        elementText("#content > article > p:nth-of-type(3) > a") shouldBe "contact us (opens in a new tab)"
      }

      "has the correct link location" in {
        element("#content > article > p:nth-of-type(3) > a").attr("href") shouldBe appConfig.govUkContactUs
      }
    }

    "have a button which" should {

      "have the correct text" in {
        elementText(".button") shouldBe "Finish"
      }

      "have the correct link location" in {
        element(".button").attr("href") shouldBe appConfig.manageVatSubscriptionServicePath
      }
    }
  }

  "The confirmation page for an agent" should {

    lazy val view = views.html.confirmation(isAgent = true)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct title" in {
      document.title shouldBe "You have opted your client out of Making Tax Digital for VAT"
    }

    "have a heading" which {

      "has the correct text" in {
        elementText("h1") shouldBe "You have opted your client out of Making Tax Digital for VAT"
      }

      "has the correct GA tag" in {
        element("h1").attr("data-journey") shouldBe "agent_opt-out:confirm:opt-out"
      }
    }

    "have the correct first paragraph" in {
      elementText("#content > article > p:nth-of-type(1)") shouldBe "You must continue to use software " +
        "compatible with Making Tax Digital to submit your client’s VAT Returns for their current return period."
    }

    "have a second paragraph" which {

      "has the correct text" in {
        elementText("#content > article > p:nth-of-type(2)") shouldBe
          "Submit your client’s VAT Returns online (opens in a new tab) from your next return period."
      }

      "has the correct link text" in {
        elementText("#content > article > p:nth-of-type(2) > a") shouldBe "online (opens in a new tab)"
      }

      "has the correct link location" in {
        element("#content > article > p:nth-of-type(2) > a").attr("href") shouldBe appConfig.govUkManageClientsDetails
      }
    }

    "have a third paragraph" which {

      "has the correct text" in {
        elementText("#content > article > p:nth-of-type(3)") shouldBe "You must contact us (opens in a new tab) " +
          "to sign your client up for Making Tax Digital again if their taxable turnover goes above £85,000."
      }

      "has the correct link text" in {
        elementText("#content > article > p:nth-of-type(3) > a") shouldBe "contact us (opens in a new tab)"
      }

      "has the correct link location" in {
        element("#content > article > p:nth-of-type(3) > a").attr("href") shouldBe appConfig.govUkContactUs
      }
    }

    "have a change client link which" should {

      "have the correct text" in {
        elementText("#content > article > p:nth-of-type(5) > a") shouldBe "Change client"
      }

      "have the correct link location" in {
        element("#content > article > p:nth-of-type(5) > a").attr("href") shouldBe appConfig.agentClientLookupHandoff
      }
    }
  }
}
