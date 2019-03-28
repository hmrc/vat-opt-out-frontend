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

class OptOutStartViewSpec extends ViewBaseSpec {

  "The opt out start journey page" should {

    lazy val view = views.html.optOutStart()
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe "Opt out of Making Tax Digital"
    }

    "have the correct heading" in {
      elementText("h1") shouldBe "Opt out of Making Tax Digital"
    }

    "have the correct paragraph" in {
      elementText("#content p") shouldBe "If you choose to opt out, you will:"
    }

    "have the correct first bullet point" in {
      elementText("li:nth-of-type(1)") shouldBe "not be deregistering from VAT"
    }

    "have the correct second bullet point" in {
      elementText("li:nth-of-type(2)") shouldBe "have to submit your VAT Returns using a different service"
    }

    "have the correct third bullet point" in {
      elementText("li:nth-of-type(3)") shouldBe "have to join again if your taxable turnover goes above the VAT threshold"
    }

    "have a continue button which" should {

      "have the correct text" in {
        elementText(".button") shouldBe "Continue"
      }

      "have the correct href" in {
        element(".button").attr("href") shouldBe "#"
      }
    }
  }
}
