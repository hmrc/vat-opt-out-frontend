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

import common.Constants.{optionNo, optionYes}
import forms.TurnoverThresholdForm.turnoverThresholdForm
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.twirl.api.Html
import views.html.TurnoverThresholdView
import models.viewModels.TurnoverThresholdViewModel

class TurnoverThresholdViewSpec extends ViewBaseSpec {

  val injectedView: TurnoverThresholdView = injector.instanceOf[TurnoverThresholdView]

  object Selectors {
    val pageHeading = "#content h1"
    val backLink = ".govuk-back-link"
    val backLinkPreviousYears = "#content > div > a"
    val hintText = ".not-hint"
    val form = "form"
    val radioYesField = "#threshold-yes"
    val radioNoField = "#threshold-no"
    val continueButton = ".govuk-button"
    val errorSummary = "#error-summary-title"
    val radioYesLabel = "label[for=threshold-yes]"
    val radioNoLabel = "label[for=threshold-no]"
    val errorSummaryLink = ".govuk-list > li:nth-child(1) > a:nth-child(1)"
    val errorRadioText = "#threshold-error"
  }

  lazy val emptyForm: Form[String] = turnoverThresholdForm(appConfig.thresholdAmount)(clientUser)
  def viewModelAgent(form: Form[String]): TurnoverThresholdViewModel = TurnoverThresholdViewModel.constructModel(form)(appConfig, agentUser)
  def viewModelClient(form: Form[String]): TurnoverThresholdViewModel = TurnoverThresholdViewModel.constructModel(form)(appConfig, clientUser)

  "Rendering the turnover threshold page with an empty form for a client" when {

    "the form has no errors" should {

      lazy val view: Html = injectedView(viewModelClient(emptyForm))(messages, appConfig, clientUser)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct document title" in {
        document.title shouldBe "Are you eligible to opt out of Making Tax Digital? - Business tax account - GOV.UK"
      }

      "have a back link" which {

        "should have the correct text" in {
          elementText(Selectors.backLink) shouldBe "Back"
        }

        "should have the correct back link" in {
          element(Selectors.backLink).attr("href") shouldBe appConfig.vatSummaryServicePath
        }
      }

      "have a page heading" which {

        "has the correct text" in {
          elementText(Selectors.pageHeading) shouldBe "Are you eligible to opt out of Making Tax Digital?"
        }
      }

      "have the correct hint text" in {
        elementText(Selectors.hintText) shouldBe "You are legally required to remain within Making Tax Digital if " +
          "your taxable turnover has been above £85,000 at any time since 1 April 2019."
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
        elementText(Selectors.radioYesLabel) shouldBe "Yes, I am eligible"
      }

      "have the correct radio button no text" in {
        elementText(Selectors.radioNoLabel) shouldBe "No, I am not eligible and want to return to the VAT account"
      }

      "have a continue button" which {

        "has the correct text" in {
          elementText(Selectors.continueButton) shouldBe "Continue"
        }
      }
    }

    "the form has errors" should {

      lazy val errorForm: Form[String] = turnoverThresholdForm(appConfig.thresholdAmount)(clientUser).bind(Map[String, String]())
      lazy val view = injectedView(viewModelClient(errorForm))(messages, appConfig, clientUser)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct title" in {
        document.title shouldBe "Error: Are you eligible to opt out of Making Tax Digital? - Business tax account - GOV.UK"
      }

      "display the error summary" in {
        element(Selectors.errorSummary).text() shouldBe "There is a problem"
      }

      "display the error summary link" which {

        "has the correct text" in {
          elementText(Selectors.errorSummaryLink) shouldBe "Select ’yes’ if you are eligible to opt out of Making Tax Digital"
        }

        "has the correct href" in {
          element(Selectors.errorSummaryLink).attr("href") shouldBe "#threshold-yes"
        }
      }

      "display the radio button error text" in {
        elementText(Selectors.errorRadioText) shouldBe "Error: Select ’yes’ if you are eligible to opt out of Making Tax Digital"
      }
    }
  }

  "Rendering the turnover threshold page for a client with a populated yes option" should {

    lazy val yesForm = turnoverThresholdForm(appConfig.thresholdAmount)(clientUser).bind(Map("threshold" -> optionYes))
    lazy val view = injectedView(viewModelClient(yesForm))(messages, appConfig, clientUser)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have a checked 'Yes' radio option" in {
      elementAttributes(Selectors.radioYesField).contains("checked")
    }

    "have an unchecked 'No' radio option" in {
      !elementAttributes(Selectors.radioNoField).contains("checked")
    }
  }

  "Rendering the turnover threshold page for a client with a populated no option" should {

    lazy val noForm = turnoverThresholdForm(appConfig.thresholdAmount)(clientUser).bind(Map("threshold" -> optionNo))
    lazy val view = injectedView(viewModelClient(noForm))(messages, appConfig, clientUser)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have a checked 'No' radio option" in {
      elementAttributes(Selectors.radioNoField).contains("checked")
    }

    "have an unchecked 'Yes' radio option" in {
      !elementAttributes(Selectors.radioYesField).contains("checked")
    }
  }

  "Rendering the turnover threshold page for an agent" when {

    "the form has no errors" should {

      lazy val view: Html = injectedView(viewModelAgent(emptyForm))(messages, appConfig, agentUser)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct document title" in {
        document.title shouldBe "Is your client eligible to opt out of Making Tax Digital? - Your client’s VAT details - GOV.UK"
      }

      "have a page heading" which {

        "has the correct text" in {
          elementText(Selectors.pageHeading) shouldBe "Is your client eligible to opt out of Making Tax Digital?"
        }
      }

      "have a back link with the correct link location" in {
        element(Selectors.backLink).attr("href") shouldBe appConfig.agentClientHubPath
      }

      "have the correct radio button yes text" in {
        elementText(Selectors.radioYesLabel) shouldBe "Yes, my client is eligible"
      }

      "have the correct radio button no text" in {
        elementText(Selectors.radioNoLabel) shouldBe "No, my client is not eligible and I want to return to the VAT account"
      }

      "have the correct hint text" in {
        elementText(Selectors.hintText) shouldBe "Your client is legally required to remain within Making Tax Digital if " +
          "their taxable turnover has been above £85,000 at any time since 1 April 2019."
      }
    }

    "the form has errors" should {

      lazy val errorForm: Form[String] = turnoverThresholdForm(appConfig.thresholdAmount)(agentUser).bind(Map[String, String]())
      lazy val view = injectedView(viewModelAgent(errorForm))(messages, appConfig, agentUser)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct title" in {
        document.title shouldBe "Error: Is your client eligible to opt out of Making Tax Digital? - Your client’s VAT details - GOV.UK"
      }

      "display the error summary link" which {

        "has the correct text" in {
          elementText(Selectors.errorSummaryLink) shouldBe "Select ’yes’ if your client is eligible to opt out of Making Tax Digital"
        }
      }

      "display the radio button error text" in {
        elementText(Selectors.errorRadioText) shouldBe "Error: Select ’yes’ if your client is eligible to opt out of Making Tax Digital"
      }
    }
  }
}
