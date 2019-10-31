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
import views.html.ConfirmOptOutView

class ConfirmOptOutViewSpec extends ViewBaseSpec {

  val injectedView: ConfirmOptOutView = injector.instanceOf[ConfirmOptOutView]

  "The Confirm Opt Out page for a client" should {

    lazy val view = injectedView()(request, messages, appConfig, clientUser)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe "Are you sure you want to opt out of Making Tax Digital for VAT? - Business tax account - GOV.UK"
    }

    "have the correct heading" in {
      elementText("h1") shouldBe "Are you sure you want to opt out of Making Tax Digital for VAT?"
    }

    "have the correct initial paragraph" in {
      elementText("#content > article > p:nth-of-type(1)") shouldBe "If you choose to opt out, you must:"
    }

    "have the correct first bullet point" in {
      elementText("#content li:nth-of-type(1)") shouldBe
        "continue to use software compatible with Making Tax Digital for your current return period"
    }

    "have the correct second bullet point" in {
      elementText("#content li:nth-of-type(2)") shouldBe
        "use your online account to submit VAT Returns from your next return period"
    }

    "have the correct third bullet point" in {
      elementText("#content li:nth-of-type(3)") shouldBe
        "contact us to sign up for Making Tax Digital if your taxable turnover goes above £85,000"
    }

    "have the correct final paragraph" in {
      elementText("#content > article > p:nth-of-type(2)") shouldBe
        "By opting out you will not be cancelling your VAT registration."
    }

    "have a button" which {

      "has the correct text" in {
        elementText(".button") shouldBe "Confirm and opt out"
      }

      "has the correct href" in {
        element(".button").attr("href") shouldBe controllers.routes.ConfirmOptOutController.updateMandationStatus().url
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

  "The Confirm Opt Out page for an agent" should {

    lazy val view = injectedView()(request, messages, appConfig, agentUser)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe "Are you sure you want to opt your client out of Making Tax Digital for VAT? " +
        "- Your client’s VAT details - GOV.UK"
    }

    "have the correct heading" in {
      elementText("h1") shouldBe "Are you sure you want to opt your client out of Making Tax Digital for VAT?"
    }

    "have the correct initial paragraph" in {
      elementText("#content > article > p:nth-of-type(1)") shouldBe "If you choose to opt your client out, you must:"
    }

    "have the correct first bullet point" in {
      elementText("#content li:nth-of-type(1)") shouldBe "continue to use software compatible with Making " +
        "Tax Digital to submit your client’s VAT Returns for their current return period"
    }

    "have the correct second bullet point" in {
      elementText("#content li:nth-of-type(2)") shouldBe
        "submit your client’s VAT Returns online from their next return period"
    }

    "have the correct third bullet point" in {
      elementText("#content li:nth-of-type(3)") shouldBe
        "sign your client up for Making Tax Digital again if their taxable turnover goes above £85,000"
    }

    "have the correct final paragraph" in {
      elementText("#content > article > p:nth-of-type(2)") shouldBe
        "By opting your client out of Making Tax Digital, you will not be cancelling their VAT registration."
    }
  }
}
