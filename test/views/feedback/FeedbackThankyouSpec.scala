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

package views.feedback


import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, verify, when}
import play.twirl.api.Html
import uk.gov.hmrc.play.partials.CachedStaticHtmlPartialRetriever
import utils.MockAuth
import views.ViewBaseSpec

class FeedbackThankyouSpec extends ViewBaseSpec with MockAuth {

  implicit val retrieve: CachedStaticHtmlPartialRetriever = mock[CachedStaticHtmlPartialRetriever]

  when(retrieve.getPartialContent(any(), any(), any())(any()))
    .thenReturn(Html("<h1>Test HTML</h1>"))

  "Rendering the feedback thank you page" should {

    lazy val view = views.html.feedback.feedback_thankyou("", "pageRedirect")
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "Have the correct title" in {
      document.title shouldBe "Thank you"
    }

    "fetch HTML from the formPartialRetriever and display it" in {
      elementText("#content h1") shouldBe "Test HTML"
    }

    "have a back link" which {

      "has the correct text" in {
        elementText("#back") shouldBe "Back"
      }

      "collects the href from the view parameter" in {
        element("#back").attr("href") shouldBe "pageRedirect"
      }
    }

    "call the staticHtmlPartialRetriever" in {
      verify(retrieve)
        .getPartialContent(any(), any(), any())(any())

      reset(retrieve)
    }
  }
}
