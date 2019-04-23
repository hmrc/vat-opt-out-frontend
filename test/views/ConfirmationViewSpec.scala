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
import assets.ContactPreferencesConstants._

class ConfirmationViewSpec extends ViewBaseSpec {

  "The confirmation page for a client with digital preference" should {

    lazy val view = views.html.confirmation(clientPreferencesDigital)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct title" in {
      document.title shouldBe "Request to opt out of Making Tax Digital for VAT received"
    }

    "have the correct heading" in {
      elementText("h1") shouldBe "Request to opt out of Making Tax Digital for VAT received"
    }

    "have the correct subheading" in {
      elementText("h2") shouldBe "What happens next"
    }

    "have the correct first paragraph" in {
      elementText("#content > article > p:nth-of-type(1)") shouldBe "We will send you an email within 2 working days " +
        "with an update, followed by a letter to your principal place of business. You can also go to your HMRC " +
        "secure messages to find out if your request has been accepted."
    }

    "have the correct second paragraph" in {
      elementText("#content > article > p:nth-of-type(2)") shouldBe "Make sure your contact details are up to date."
    }

    "have a button which" should {

      "have the correct text" in {
        elementText(".button") shouldBe "Finish"
      }

      "have the correct href" in {
        element(".button").attr("href") shouldBe appConfig.manageVatSubscriptionServicePath
      }
    }
  }

  "The confirmation page for a client with paper preference" should {

    lazy val view = views.html.confirmation(clientPreferencesPaper)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct title" in {
      document.title shouldBe "Request to opt out of Making Tax Digital for VAT received"
    }

    "have the correct heading" in {
      elementText("h1") shouldBe "Request to opt out of Making Tax Digital for VAT received"
    }

    "have the correct subheading" in {
      elementText("h2") shouldBe "What happens next"
    }

    "have the correct first paragraph" in {
      elementText("#content > article > p:nth-of-type(1)") shouldBe
        "We will send a letter to your principal place of business with an update within 15 working days."
    }

    "have the correct second paragraph" in {
      elementText("#content > article > p:nth-of-type(2)") shouldBe "Make sure your contact details are up to date."
    }

    "have a button which" should {

      "have the correct text" in {
        elementText(".button") shouldBe "Finish"
      }

      "have the correct href" in {
        element(".button").attr("href") shouldBe appConfig.manageVatSubscriptionServicePath
      }
    }
  }

  "The confirmation page for a client where the preference could not be obtained" should {

    lazy val view = views.html.confirmation(clientPreferencesFail)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct title" in {
      document.title shouldBe "Request to opt out of Making Tax Digital for VAT received"
    }

    "have the correct heading" in {
      elementText("h1") shouldBe "Request to opt out of Making Tax Digital for VAT received"
    }

    "have the correct subheading" in {
      elementText("h2") shouldBe "What happens next"
    }

    "have the correct first paragraph" in {
      elementText("#content > article > p:nth-of-type(1)") shouldBe "We will send you an update within 15 working days."
    }

    "have the correct second paragraph" in {
      elementText("#content > article > p:nth-of-type(2)") shouldBe "Make sure your contact details are up to date."
    }

    "have a button which" should {

      "have the correct text" in {
        elementText(".button") shouldBe "Finish"
      }

      "have the correct href" in {
        element(".button").attr("href") shouldBe appConfig.manageVatSubscriptionServicePath
      }
    }
  }

  "The confirmation page for a transactor with digital preference" should {

    lazy val view = views.html.confirmation(agentPreferencesDigital)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct title" in {
      document.title shouldBe "Request to opt out of Making Tax Digital for VAT received"
    }

    "have the correct heading" in {
      elementText("h1") shouldBe "Request to opt out of Making Tax Digital for VAT received"
    }

    "have the correct subheading" in {
      elementText("h2") shouldBe "What happens next"
    }

    "have the correct first paragraph" in {
      elementText("#content > article > p:nth-of-type(1)") shouldBe "We will send an email to test@test.com " +
        "within 2 working days telling you whether or not the request has been accepted."
    }

    "have the correct second paragraph" in {
      elementText("#content > article > p:nth-of-type(2)") shouldBe "We will also contact Acme ltd with an update."
    }

    "have a button which" should {

      "have the correct text" in {
        elementText(".button") shouldBe "Finish"
      }

      "have the correct href" in {
        element(".button").attr("href") shouldBe appConfig.manageVatSubscriptionServicePath
      }
    }
  }

  "The confirmation page for a transactor with digital preference but no business name" should {

    lazy val view = views.html.confirmation(agentPreferencesDigitalNoBusinessName)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct title" in {
      document.title shouldBe "Request to opt out of Making Tax Digital for VAT received"
    }

    "have the correct heading" in {
      elementText("h1") shouldBe "Request to opt out of Making Tax Digital for VAT received"
    }

    "have the correct subheading" in {
      elementText("h2") shouldBe "What happens next"
    }

    "have the correct first paragraph" in {
      elementText("#content > article > p:nth-of-type(1)") shouldBe "We will send an email to test@test.com " +
        "within 2 working days telling you whether or not the request has been accepted."
    }

    "have the correct second paragraph" in {
      elementText("#content > article > p:nth-of-type(2)") shouldBe "We will also contact your client with an update."
    }

    "have a button which" should {

      "have the correct text" in {
        elementText(".button") shouldBe "Finish"
      }

      "have the correct href" in {
        element(".button").attr("href") shouldBe appConfig.manageVatSubscriptionServicePath
      }
    }
  }

  "The confirmation page for a transactor with paper preference" should {

    lazy val view = views.html.confirmation(agentPreferencesPaper)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct title" in {
      document.title shouldBe "Request to opt out of Making Tax Digital for VAT received"
    }

    "have the correct heading" in {
      elementText("h1") shouldBe "Request to opt out of Making Tax Digital for VAT received"
    }

    "have the correct subheading" in {
      elementText("h2") shouldBe "What happens next"
    }

    "have the correct first paragraph" in {
      elementText("#content > article > p:nth-of-type(1)") shouldBe
        "We will send a confirmation letter to the agency address within 15 working days."
    }

    "have the correct second paragraph" in {
      elementText("#content > article > p:nth-of-type(2)") shouldBe "We will also contact Acme ltd with an update."
    }

    "have a button which" should {

      "have the correct text" in {
        elementText(".button") shouldBe "Finish"
      }

      "have the correct href" in {
        element(".button").attr("href") shouldBe appConfig.manageVatSubscriptionServicePath
      }
    }
  }

  "The confirmation page for a transactor with paper preference but no business name" should {

    lazy val view = views.html.confirmation(agentPreferencesPaperNoBusinessName)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct title" in {
      document.title shouldBe "Request to opt out of Making Tax Digital for VAT received"
    }

    "have the correct heading" in {
      elementText("h1") shouldBe "Request to opt out of Making Tax Digital for VAT received"
    }

    "have the correct subheading" in {
      elementText("h2") shouldBe "What happens next"
    }

    "have the correct first paragraph" in {
      elementText("#content > article > p:nth-of-type(1)") shouldBe
        "We will send a confirmation letter to the agency address within 15 working days."
    }

    "have the correct second paragraph" in {
      elementText("#content > article > p:nth-of-type(2)") shouldBe "We will also contact your client with an update."
    }

    "have a button which" should {

      "have the correct text" in {
        elementText(".button") shouldBe "Finish"
      }

      "have the correct href" in {
        element(".button").attr("href") shouldBe appConfig.manageVatSubscriptionServicePath
      }
    }
  }
}
