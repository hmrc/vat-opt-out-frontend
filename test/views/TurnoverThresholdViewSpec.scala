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
import play.twirl.api.Html
import forms.TurnoverThresholdForm.turnoverThresholdForm
import play.api.data.Form
import common.Constants.{optionNo, optionYes}

class TurnoverThresholdViewSpec extends ViewBaseSpec {

  object Selectors {
    val pageHeading = "#content h1"
    val backLink = "#content > article > a"
    val backLinkPreviousYears = "#content > article > div > a"
    val hintText = "#label-email-hint"
    val form = "form"
    val radioYesField = "#threshold-yes"
    val radioNoField = "#threshold-no"
    val continueButton = "button"
    val errorSummary = "#error-summary-heading"
    val radioYesLabel = "label[for=threshold-yes]"
    val radioNoLabel = "label[for=threshold-no]"
    val errorSummaryLink = "#threshold-error-summary"
    val errorRadioText = "span.error-notification"
  }

  val emptyForm: Form[String] = turnoverThresholdForm

  "Rendering the turnover threshold page with an empty form for a client" when {

    "the form has no errors" should {

      lazy val view: Html = views.html.turnoverThreshold(emptyForm, isAgent = false)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct document title" in {
        document.title shouldBe "Was the business’s taxable turnover above £85,000 since 1st April 2019?"
      }

      "have a back link" which {

        "should have the correct text" in {
          elementText(Selectors.backLink) shouldBe "Back"
        }

        "should have the correct back link" in {
          element(Selectors.backLink).attr("href") shouldBe appConfig.manageVatSubscriptionServicePath
        }
      }

      "have a previous years link" which {

        "should have the correct text" in {
          elementText(Selectors.backLinkPreviousYears) shouldBe "View the VAT threshold for previous tax years (opens in a new tab)"
        }

        "should have the correct previous years link" in {
          element(Selectors.backLinkPreviousYears).attr("href") shouldBe "/some-link"

        }
      }

      "have a page heading" which {

        "has the correct text" in {
          elementText(Selectors.pageHeading) shouldBe "Was the business’s taxable turnover above £85,000 since 1st April 2019?"
        }

        "has the correct GA tag" in {
          element(Selectors.pageHeading).attr("data-journey") shouldBe "opt-out:view:change-opt-out"
        }
      }

      "have the turnover threshold form with the correct form action" in {
        element(Selectors.form).attr("action") shouldBe controllers.routes.TurnoverThresholdController.submit().url
      }

      "have the radio button no field that have the correct post value" in {
        element(Selectors.radioNoField).attr("value") shouldBe optionNo
      }

      "have the radio button yes field that have the correct post value" in {
        element(Selectors.radioYesField).attr("value") shouldBe optionYes
      }

      "have the correct radio button yes text" in {
        elementText(Selectors.radioYesLabel) shouldBe "Yes"
      }

      "have the correct radio button no text" in {
        elementText(Selectors.radioNoLabel) shouldBe "No"
      }

      "have a continue button" which {

        "has the correct text" in {
          elementText(Selectors.continueButton) shouldBe "Continue"
        }

        "has the correct GA tag" in {
          element(Selectors.continueButton).attr("data-journey-click") shouldBe "opt-out:continue:opt-out"
        }
      }
    }

    "the form has errors" should {

      lazy val view = views.html.turnoverThreshold(turnoverThresholdForm.bind(Map("threshold" -> "")), isAgent = false)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "display the error summary" in {
        element(Selectors.errorSummary).text() shouldBe "There is a problem"
      }

      "display the error summary link" in {
        elementText(Selectors.errorSummaryLink) shouldBe "Select yes if the business’s taxable turnover is above £85,000"
      }

      "display the radio button error text" in {
        elementText(Selectors.errorRadioText) shouldBe "Select yes if the business’s taxable turnover is above £85,000"
      }
    }
  }

  "Rendering the turnover threshold page for a client with a populated yes option" should {

    lazy val view = views.html.turnoverThreshold(turnoverThresholdForm.bind(Map("threshold" -> optionYes)), isAgent = false)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have a 'Yes' radio button" which {

      "should be selected" in {
        element(Selectors.radioYesField).attr("checked") shouldBe "checked"
      }
    }
  }

  "Rendering the turnover threshold page for a client with aa populated no value" should {

    lazy val view = views.html.turnoverThreshold(turnoverThresholdForm.bind(Map("threshold" -> optionNo)), isAgent = false)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have a 'No' radio button" which {

      "should be selected" in {
        element(Selectors.radioNoField).attr("checked") shouldBe "checked"
      }
    }
  }

  "Rendering the turnover threshold page for an agent" should {

    lazy val view: Html = views.html.turnoverThreshold(emptyForm, isAgent = true)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct GA tag on the heading" in {
      element(Selectors.pageHeading).attr("data-journey") shouldBe "agent_opt-out:view:change-opt-out"
    }

    "have the correct GA tag on the continue button" in {
      element(Selectors.continueButton).attr("data-journey-click") shouldBe "agent_opt-out:continue:opt-out"
    }
  }
}
