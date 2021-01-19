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

package views.components

import utils.TestUtils

class FooterLinksSpec extends TestUtils {

  val footerLinks = new FooterLinks()

  "FooterLinks" should {

    "generate the correct first footer item" which {

      "has the correct text" in {
        footerLinks.cookiesLink().text shouldBe Some("Cookies")
      }

      "has the correct href" in {
        footerLinks.cookiesLink().href shouldBe Some(appConfig.footerCookiesUrl)
      }
    }

    "generate the correct second footer item" which {

      "has the correct text" in {
        footerLinks.accessibilityLink().text shouldBe Some("Accessibility")
      }

      "has the correct href" in {
        footerLinks.accessibilityLink().href shouldBe Some(appConfig.footerAccessibilityUrl)
      }
    }

    "generate the correct third footer item" which {

      "has the correct text" in {
        footerLinks.privacyLink().text shouldBe Some("Privacy policy")
      }

      "has the correct href" in {
        footerLinks.privacyLink().href shouldBe Some(appConfig.footerPrivacyUrl)
      }
    }

    "generate the correct fourth footer item" which {

      "has the correct text" in {
        footerLinks.termsConditionsLink().text shouldBe Some("Terms and conditions")
      }

      "has the correct href" in {
        footerLinks.termsConditionsLink().href shouldBe Some(appConfig.footerTermsConditionsUrl)
      }
    }

    "generate the correct fifth footer item" which {

      "has the correct text" in {
        footerLinks.govukHelpLink().text shouldBe Some("Help using GOV.UK")
      }

      "has the correct href" in {
        footerLinks.govukHelpLink().href shouldBe Some(appConfig.footerHelpUrl)
      }
    }

    "generate a list with the footer items in the correct order" in {
      footerLinks.items shouldBe Seq(
        footerLinks.cookiesLink(),
        footerLinks.accessibilityLink(),
        footerLinks.privacyLink(),
        footerLinks.termsConditionsLink(),
        footerLinks.govukHelpLink()
      )
    }
  }
}
