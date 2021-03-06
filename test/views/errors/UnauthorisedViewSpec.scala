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

package views.errors

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.html.errors.UnauthorisedView
import views.ViewBaseSpec

class UnauthorisedViewSpec extends ViewBaseSpec {

  val injectedView: UnauthorisedView = injector.instanceOf[UnauthorisedView]

  "Rendering the unauthorised page" should {

    lazy val view = injectedView()
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe "You are not authorised to use this service - VAT - GOV.UK"
    }

    "have the correct page heading" in {
      elementText("#content h1") shouldBe "You are not authorised to use this service"
    }

    "have the correct instructions on the page" in {
      elementText("#content p") shouldBe "You need to sign up to use software to submit your VAT Returns."
    }

    "have a button to sign out" which {

      "has the correct text" in {
        elementText(".govuk-button") shouldBe "Sign out"
      }

      "have the correct href" in {
        element(".govuk-button").attr("href") shouldBe controllers.routes.SignOutController.signOut(authorised = false).url
      }
    }
  }
}
