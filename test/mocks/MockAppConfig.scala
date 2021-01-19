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

package mocks

import config.AppConfig
import play.api.i18n.Lang
import play.api.mvc.Call
import play.api.{Configuration, Mode}

class MockAppConfig(val runModeConfiguration: Configuration, val mode: Mode = Mode.Test) extends AppConfig {
  override val contactHost = ""
  override val contactFormServiceIdentifier: String = ""
  override val assetsPrefix = ""
  override val vatOptOutServicePath = "/opt-out"
  override val signInUrl = "/sign-in-url"
  override def signOutUrl(identifier: String): String = s"/sign-out/$identifier"
  override val unauthorisedSignOutUrl: String = "/unauthorised-sign-out"
  override val manageVatSubscriptionServiceUrl: String = ""
  override val manageVatSubscriptionServicePath: String = "/change-business-details"
  override val thresholdPreviousYearsUrl: String = "/some-link"
  override val vatSubscriptionHost: String = "vat-subscription"
  override val agentClientLookupHandoff: String = "/agent-client-lookup"
  override val agentClientHubPath: String = "/representative/client-vat-account"
  override val vatAgentClientLookupUnauthorised: String = "mockUnauthVACLF"
  override val footerAccessibilityUrl: String = "/accessibility-statement"
  override def feedbackUrl(redirect: String): String = s"feedback/$redirect"
  override def exitSurveyUrl(identifier: String): String = s"/exit-survey/$identifier"
  override val agentServicesGovUkGuidance: String = "/agent-guidance"
  override val timeoutPeriod: Int = 999
  override val timeoutCountdown: Int = 999
  override def routeToSwitchLanguage: String => Call = (lang: String) => controllers.routes.LanguageController.switchToLanguage(lang)
  override def languageMap: Map[String, Lang] = Map(
    "english" -> Lang("en"),
    "cymraeg" -> Lang("cy")
  )
  override val govUkManageClientsDetails: String = "/gov-uk/manage"
  override val govUkContactUs: String = "/gov-uk/contact-us"
  override val thresholdAmount: String = "£85,000"
  override val vatSummaryServicePath: String = "/vat-summary"
  override val gtmContainer: String = "x"
  override val footerCookiesUrl: String = "/cookies"
  override val footerPrivacyUrl: String = "/privacy"
  override val footerTermsConditionsUrl: String = "/terms"
  override val footerHelpUrl: String = "/help"
}
