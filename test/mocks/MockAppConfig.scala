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

package mocks

import play.api.mvc.Call
import config.AppConfig
import play.api.{Configuration, Mode}
import play.api.Mode.Mode
import play.api.i18n.Lang

class MockAppConfig(val runModeConfiguration: Configuration, val mode: Mode = Mode.Test) extends AppConfig {
  override val contactHost = ""
  override val assetsPrefix = ""
  override val analyticsToken = ""
  override val analyticsHost = ""
  override val reportAProblemPartialUrl = ""
  override val reportAProblemNonJSUrl = ""
  override val whitelistEnabled: Boolean = false
  override val whitelistedIps: Seq[String] = Seq("")
  override val whitelistExcludedPaths: Seq[Call] = Nil
  override val shutterPage: String = "https://www.tax.service.gov.uk/shutter/vat-change-of-circumstances"
  override val vatOptOutServicePath = "/opt-out"
  override val signInUrl = "/sign-in-url"
  override val signOutUrl: String = "/sign-out"
  override val unauthorisedSignOutUrl: String = "/unauthorised-sign-out"
  override val manageVatSubscriptionServiceUrl: String = ""
  override val manageVatSubscriptionServicePath: String = "/change-business-details"
  override val thresholdPreviousYearsUrl: String = "/some-link"
  override val vatSubscriptionHost: String = "vat-subscription"
  override val contactPreferencesHost: String = "/test-contact-preferences-host"
  override val agentClientLookupHandoff: String = "/agent-client-lookup"
  override val agentInvitationsFastTrack: String = "/agent-fast-track"
  override def feedbackUrl(redirect: String): String = s"feedback/$redirect"
  override val exitSurveyUrl: String = "/exit-survey"
  override val agentServicesGovUkGuidance: String = "/agent-guidance"
  override val timeoutPeriod: Int = 999
  override val timeoutCountdown: Int = 999
  override def routeToSwitchLanguage: String => Call = (lang: String) => controllers.routes.LanguageController.switchToLanguage(lang)
  override def languageMap: Map[String, Lang] = Map(
    "english" -> Lang("en"),
    "cymraeg" -> Lang("cy")
  )
}
