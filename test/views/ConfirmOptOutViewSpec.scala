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
import forms.ConfirmOptOutForm._

class ConfirmOptOutViewSpec extends ViewBaseSpec {

  lazy val pageTitleAndHeading = "Are you sure you want to opt out from Making Tax Digital for VAT?"
  lazy val continueUrl = "/vat-through-software/account/opt-out/confirm-opt-out"

  object Selectors {
    val pageHeading = "#content h1"
    val subtext = "fieldset > p"
    val backLink = "#content > article > a"
    val form = "form"
    val confirmButton = "button"
    val errorSummaryHeading = "#error-summary-heading"
    val errorSummaryLink = "#confirmOptOut-error-summary"
    val radioOptionYes = "#confirmOptOut-yes"
    val radioOptionNo = "#confirmOptOut-no"
    val errorText = ".error-notification"
  }

  "Rendering the ConfirmOptOut page" when {

    "the user doesn't have a radio option selected" should {

      lazy val view: Html = views.html.confirmOptOut(confirmOptOutForm)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct document title" in {
        document.title shouldBe pageTitleAndHeading
      }

      "have a back link which takes the user to the opt out start page" in {
        element(Selectors.backLink).attr("href") shouldBe controllers.routes.TurnoverThresholdController.show().url
      }

      "have the correct page heading" in {
        elementText(Selectors.pageHeading) shouldBe pageTitleAndHeading
      }

      "have the correct form action" in {
        element(Selectors.form).attr("action") shouldBe continueUrl
      }

      "have the yes radio option" in {
        element(Selectors.radioOptionYes).attr("value") shouldBe "yes"
      }

      "have the no radio option" in {
        element(Selectors.radioOptionNo).attr("value") shouldBe "no"
      }

      "have the confirm button" in {
        elementText(Selectors.confirmButton) shouldBe "Confirm"
      }
    }

    "the user has already selected the no radio button" should {

      lazy val view: Html = views.html.confirmOptOut(confirmOptOutForm.bind(
        Map("confirmOptOut" -> "no")))
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct document title" in {
        document.title shouldBe pageTitleAndHeading
      }

      "have a back link which takes the user to the opt out start page" in {
        element(Selectors.backLink).attr("href") shouldBe controllers.routes.TurnoverThresholdController.show().url
      }

      "have the correct page heading" in {
        elementText(Selectors.pageHeading) shouldBe pageTitleAndHeading
      }

      "have the correct form action" in {
        element(Selectors.form).attr("action") shouldBe continueUrl
      }

      "have the yes radio option" in {
        element(Selectors.radioOptionYes).attr("value") shouldBe "yes"
      }

      "have the no radio option" in {
        element(Selectors.radioOptionNo).attr("value") shouldBe "no"
      }

      "have the no radio option selected" in {
        element(Selectors.radioOptionNo).attr("checked") shouldBe "checked"
      }

      "not have the yes radio option selected" in {
        element(Selectors.radioOptionYes).hasAttr("checked") shouldBe false
      }

      "have the continue button" in {
        elementText(Selectors.confirmButton) shouldBe "Confirm"
      }

    }

    "the user has already selected the yes radio button" should {

      lazy val view: Html = views.html.confirmOptOut(confirmOptOutForm.bind(
        Map("confirmOptOut" -> "yes")))
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct document title" in {
        document.title shouldBe pageTitleAndHeading
      }

      "have a back link which takes the user to the opt out start page" in {
        element(Selectors.backLink).attr("href")shouldBe controllers.routes.TurnoverThresholdController.show().url
      }

      "have the correct page heading" in {
        elementText(Selectors.pageHeading) shouldBe pageTitleAndHeading
      }

      "have the correct form action" in {
        element(Selectors.form).attr("action") shouldBe continueUrl
      }

      "have the yes radio option" in {
        element(Selectors.radioOptionYes).attr("value") shouldBe "yes"
      }

      "have the no radio option" in {
        element(Selectors.radioOptionNo).attr("value") shouldBe "no"
      }

      "have the yes radio option selected" in {
        element(Selectors.radioOptionYes).attr("checked") shouldBe "checked"
      }

      "not have the no radio option selected" in {
        element(Selectors.radioOptionNo).hasAttr("checked") shouldBe false
      }

      "have the continue button" in {
        elementText(Selectors.confirmButton) shouldBe "Confirm"
      }

    }

    "the form is posted with no option selected" should {
      lazy val view = views.html.confirmOptOut(confirmOptOutForm.bind(Map("" -> "")))
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "display the error summary" in {
        element(Selectors.errorSummaryHeading).text() shouldBe "There is a problem"
      }

      "display a link to the error field" in {
        element(Selectors.errorSummaryLink).attr("href") shouldBe "#confirmOptOut"
      }

    }
  }
}
