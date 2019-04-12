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
import uk.gov.hmrc.play.partials.FormPartialRetriever
import utils.MockAuth
import views.ViewBaseSpec
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{never, verify, when}
import play.twirl.api.Html
import org.mockito.Mockito.reset

class FeedbackSpec extends ViewBaseSpec with MockAuth {

  implicit val retrieve: FormPartialRetriever = mock[FormPartialRetriever]

  when(retrieve.getPartialContent(any(), any(), any())(any()))
    .thenReturn(Html("<h1>Test HTML</h1>"))

  "Rendering the feedback page" when {
    "no form body is provided" should {

      lazy val view = views.html.feedback.feedback("", None)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "Have the correct title" in {
        document.title shouldBe "Feedback"
      }

      "fetch HTML from the formPartialRetriever and display it" in {
        elementText("#content h1") shouldBe "Test HTML"
      }

      "call the formPartialRetriever" in {
        verify(retrieve)
          .getPartialContent(any(), any(), any())(any())

        reset(retrieve)
      }
    }

    "a form body is provided" should {

      lazy val view = views.html.feedback.feedback("", Some(Html("<h1>Static Test HTML</h1>")))
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "Have the correct title" in {
        document.title shouldBe "Feedback"
      }

      "fetch HTML from the formPartialRetriever and display it" in {
        elementText("#content h1") shouldBe "Static Test HTML"
      }

      "call the formPartialRetriever" in {
        verify(retrieve, never())
          .getPartialContent(any(), any(), any())(any())
      }

    }
  }
}
