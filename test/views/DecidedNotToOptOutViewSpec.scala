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

class DecidedNotToOptOutViewSpec extends ViewBaseSpec {

  "The decided not to opt out view spec" should {

    lazy val view = views.html.decidedNotToOptOut()
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct title" in {
      document.title shouldBe "You have decided not to opt out of Making Tax Digital"
    }

    "have the correct heading" in {
      elementText("h1") shouldBe "You have decided not to opt out of Making Tax Digital"
    }

    "have a button" which {

      "has the correct text" in {
        elementText(".button") shouldBe "Return to change of business details"
      }

      "has the correct href" in {
        element(".button").attr("href") shouldBe appConfig.manageVatUrl
      }
    }

    "have a back link" which {

      "has the correct text" in {
        elementText(".link-back") shouldBe "Back"
      }

      "has the correct href" in {
        element(".link-back").attr("href") shouldBe controllers.routes.ConfirmOptOutController.show().url
      }
    }
  }
}
