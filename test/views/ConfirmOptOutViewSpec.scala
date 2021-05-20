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
import views.html.ConfirmOptOutView

class ConfirmOptOutViewSpec extends ViewBaseSpec {

  val injectedView: ConfirmOptOutView = injector.instanceOf[ConfirmOptOutView]
  val assistiveWarning = "Warning "

  "The Confirm Opt Out page for a client" should {

    lazy val view = injectedView()(messages, appConfig, clientUser)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe "Are you sure you want to opt out of Making Tax Digital for VAT? - Business tax account - GOV.UK"
    }

    "have the correct heading" in {
      elementText("h1") shouldBe "Are you sure you want to opt out of Making Tax Digital for VAT?"
    }

    "have the correct initial paragraph" in {
      elementText("#content > p:nth-of-type(1)") shouldBe "If you choose to opt out you will:"
    }

    "have the correct first bullet point" in {
      elementText("#content li:nth-of-type(1)") shouldBe
        "remain VAT registered"
    }

    "have the correct second bullet point" in {
      elementText("#content li:nth-of-type(2)") shouldBe
        "have to submit your VAT returns using your online account"
    }

    "have the correct third bullet point" in {
      elementText("#content li:nth-of-type(3)") shouldBe
        "be required to use compatible software again if your taxable turnover goes over the VAT threshold"
    }

    "have the correct warning message" in {
      elementText("#content > div > strong") shouldBe
        assistiveWarning + "You are legally obliged to submit your VAT returns using compatible software as soon as your taxable turnover goes above £85,000"
    }

    "have a button" which {

      "has the correct text" in {
        elementText(".govuk-button") shouldBe "Confirm and opt out"
      }

      "has the correct href" in {
        element(".govuk-button").attr("href") shouldBe controllers.routes.ConfirmOptOutController.updateMandationStatus().url
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

  "The Confirm Opt Out page for an agent" should {

    lazy val view = injectedView()(messages, appConfig, agentUser)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe "Are you sure you want to opt your client out of Making Tax Digital for VAT? " +
        "- Your client’s VAT details - GOV.UK"
    }

    "have the correct heading" in {
      elementText("h1") shouldBe "Are you sure you want to opt your client out of Making Tax Digital for VAT?"
    }

    "have the correct initial paragraph" in {
      elementText("#content > p:nth-of-type(1)") shouldBe "If you choose to opt your client out they will:"
    }

    "have the correct first bullet point" in {
      elementText("#content li:nth-of-type(1)") shouldBe "remain VAT registered"
    }

    "have the correct second bullet point" in {
      elementText("#content li:nth-of-type(2)") shouldBe
        "have to submit VAT returns using their online account"
    }

    "have the correct third bullet point" in {
      elementText("#content li:nth-of-type(3)") shouldBe
        "be required to use compatible software again if their taxable turnover goes over the VAT threshold"
    }

    "have the correct warning message" in {
      elementText("#content > div > strong") shouldBe
        assistiveWarning + "Your client is legally obliged to submit VAT returns using compatible software as soon as their taxable turnover goes above £85,000"
    }
  }
}
