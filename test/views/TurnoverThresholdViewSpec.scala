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

  "Rendering the turnover threshold page with an empty form" when {
    "the form has no errors" should {

      lazy val view: Html = views.html.turnoverThreshold(emptyForm)

      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct document title" in {
        document.title shouldBe "Was the business’s taxable turnover above £85,000 since 1st April 2019?"
      }

      "have a back link" which {

        "should have the correct text" in {
          elementText(Selectors.backLink) shouldBe "Back"
        }

        "should have the correct back link" in {
          element(Selectors.backLink).attr("href") shouldBe controllers.routes.OptOutStartController.show().url
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

      "have the correct page heading" in {
        elementText(Selectors.pageHeading) shouldBe "Was the business’s taxable turnover above £85,000 since 1st April 2019?"
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

      "have the continue button" in {
        elementText(Selectors.continueButton) shouldBe "Continue"
      }

    }
  }

  "Rendering the turnover threshold page with an populated yes option" when {
    "the form has no errors" should {

      lazy val view = views.html.turnoverThreshold(turnoverThresholdForm.bind(Map("threshold" -> optionYes)))
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct document title" in {
        document.title shouldBe "Was the business’s taxable turnover above £85,000 since 1st April 2019?"
      }

      "have a back link" which {

        "should have the correct text" in {
          elementText(Selectors.backLink) shouldBe "Back"
        }

        "should have the correct back link" in {
          element(Selectors.backLink).attr("href") shouldBe controllers.routes.OptOutStartController.show().url
        }
      }

      "have the yes value" which {
        "should have the yes value checked so it is pre selected" in {
          element(Selectors.radioYesField).attr("checked") shouldBe "checked"

        }
      }

      "have a previous years link" which {

        "should have the correct text" in {
          elementText(Selectors.backLinkPreviousYears) shouldBe "View the VAT threshold for previous tax years (opens in a new tab)"
        }

        "should have the correct previous years link" in {
          element(Selectors.backLinkPreviousYears).attr("href") shouldBe "/some-link"

        }

        "should have the yes value pre selected" in {
          element(Selectors.radioYesField).attr("checked") shouldBe "checked"

        }
      }

      "have the correct page heading" in {
        elementText(Selectors.pageHeading) shouldBe "Was the business’s taxable turnover above £85,000 since 1st April 2019?"
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

      "have the continue button" in {
        elementText(Selectors.continueButton) shouldBe "Continue"
      }

    }
  }

  "Rendering the turnover threshold page with an populated no value" when {
    "the form has no errors" should {

      lazy val view = views.html.turnoverThreshold(turnoverThresholdForm.bind(Map("threshold" -> optionNo)))
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct document title" in {
        document.title shouldBe "Was the business’s taxable turnover above £85,000 since 1st April 2019?"
      }

      "have a back link" which {

        "should have the correct text" in {
          elementText(Selectors.backLink) shouldBe "Back"
        }

        "should have the correct back link" in {
          element(Selectors.backLink).attr("href") shouldBe controllers.routes.OptOutStartController.show().url
        }
      }

      "have the no value" which {
        "should have the no value checked so it is pre selected" in {
          element(Selectors.radioNoField).attr("checked") shouldBe "checked"

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

      "have the correct page heading" in {
        elementText(Selectors.pageHeading) shouldBe "Was the business’s taxable turnover above £85,000 since 1st April 2019?"
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

      "have the continue button" in {
        elementText(Selectors.continueButton) shouldBe "Continue"
      }

    }
  }

  "Rendering the turnover threshold page" when {
    "the form has errors" should {

      lazy val view = views.html.turnoverThreshold(turnoverThresholdForm.bind(Map("threshold" -> "")))
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct document title" in {
        document.title shouldBe "Was the business’s taxable turnover above £85,000 since 1st April 2019?"
      }

      "have a back link" which {

        "should have the correct text" in {
          elementText(Selectors.backLink) shouldBe "Back"
        }

        "should have the correct back link" in {
          element(Selectors.backLink).attr("href") shouldBe controllers.routes.OptOutStartController.show().url
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

      "have the correct page heading" in {
        elementText(Selectors.pageHeading) shouldBe "Was the business’s taxable turnover above £85,000 since 1st April 2019?"
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

      "have the continue button" in {
        elementText(Selectors.continueButton) shouldBe "Continue"
      }

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
}
