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

package config

import java.util.Base64

import config.{ConfigKeys => Keys}
import javax.inject.{Inject, Singleton}
import play.api.{Configuration, Environment}
import play.api.Mode.Mode
import play.api.i18n.Lang
import play.api.mvc.Call
import uk.gov.hmrc.play.binders.ContinueUrl
import uk.gov.hmrc.play.config.ServicesConfig

trait AppConfig extends ServicesConfig {
  val contactHost: String
  val assetsPrefix: String
  val analyticsToken: String
  val analyticsHost: String
  val reportAProblemPartialUrl: String
  val reportAProblemNonJSUrl: String
  val whitelistEnabled: Boolean
  val whitelistedIps: Seq[String]
  val whitelistExcludedPaths: Seq[Call]
  val shutterPage: String
  val vatOptOutServicePath: String
  val agentClientLookupHandoff: String
  val agentClientLookupChoicesPath: String
  val signInUrl: String
  val signOutUrl: String
  val unauthorisedSignOutUrl: String
  val manageVatSubscriptionServiceUrl: String
  val manageVatSubscriptionServicePath: String
  val thresholdPreviousYearsUrl: String
  val vatSubscriptionHost: String
  def feedbackUrl(redirect: String): String
  val exitSurveyUrl: String
  val agentServicesGovUkGuidance: String
  val timeoutPeriod: Int
  val timeoutCountdown: Int
  def routeToSwitchLanguage: String => Call
  def languageMap: Map[String, Lang]
  val agentInvitationsFastTrack: String
  val govUkManageClientsDetails: String
  val govUkContactUs: String
  val thresholdAmount: String
  val vatSummaryServicePath: String
}

@Singleton
class FrontendAppConfig @Inject()(val runModeConfiguration: Configuration, environment: Environment) extends AppConfig {
  override protected def mode: Mode = environment.mode

  lazy val contactHost: String = getString(s"contact-frontend.url")
  lazy val contactFormServiceIdentifier = "VATC"

  lazy val assetsPrefix: String = getString(s"assets.url") + getString(s"assets.version")
  lazy val analyticsToken: String = getString(s"google-analytics.token")
  lazy val analyticsHost: String = getString(s"google-analytics.host")

  lazy val reportAProblemPartialUrl = s"$contactHost/contact/problem_reports_ajax?service=$contactFormServiceIdentifier"
  lazy val reportAProblemNonJSUrl = s"$contactHost/contact/problem_reports_nonjs?service=$contactFormServiceIdentifier"

  private lazy val governmentGatewayHost: String = getString(Keys.governmentGatewayHost)

  private lazy val signInContinueUrl: String = ContinueUrl(vatOptOutServicePath).encodedUrl
  private lazy val signInBaseUrl: String = getString(Keys.signInBaseUrl)
  private lazy val signInOrigin = getString("appName")
  override lazy val signInUrl: String = s"$signInBaseUrl?continue=$signInContinueUrl&origin=$signInOrigin"

  override lazy val signOutUrl: String = s"$governmentGatewayHost/gg/sign-out?continue=$exitSurveyUrl"
  override lazy val unauthorisedSignOutUrl: String = s"$governmentGatewayHost/gg/sign-out?continue=$signInContinueUrl"

  private def whitelistConfig(key: String): Seq[String] = Some(new String(Base64.getDecoder
    .decode(getString(key)), "UTF-8"))
    .map(_.split(",")).getOrElse(Array.empty).toSeq

  override lazy val whitelistEnabled: Boolean = getBoolean(Keys.whitelistEnabled)
  override lazy val whitelistedIps: Seq[String] = whitelistConfig(Keys.whitelistedIps)
  override lazy val whitelistExcludedPaths: Seq[Call] = whitelistConfig(Keys.whitelistExcludedPaths).map(path => Call("GET", path))
  override lazy val shutterPage: String = getString(Keys.whitelistShutterPage)

  private lazy val vatOptOutServiceUrl: String = getString(Keys.vatOptOutServiceUrl)
  override lazy val vatOptOutServicePath: String =
    vatOptOutServiceUrl + getString(Keys.vatOptOutServicePath)

  private lazy val agentClientLookupServiceUrl: String = getString(Keys.agentClientLookupUrl)
  private lazy val agentClientLookupServicePath: String =
    agentClientLookupServiceUrl + getString(Keys.agentClientLookupPath)
  override lazy val agentClientLookupHandoff: String =
    agentClientLookupServicePath + s"/client-vat-number?redirectUrl=$signInContinueUrl"
  override lazy val agentClientLookupChoicesPath: String =
    agentClientLookupServicePath + getString(Keys.agentClientLookupChoices)

  override lazy val manageVatSubscriptionServiceUrl: String = getString(Keys.manageVatSubscriptionServiceUrl)
  override lazy val manageVatSubscriptionServicePath: String =
    manageVatSubscriptionServiceUrl + getString(Keys.manageVatSubscriptionServicePath)

  override val thresholdPreviousYearsUrl: String = getString(Keys.thresholdPreviousYearsUrl)
  override val vatSubscriptionHost: String = baseUrl(Keys.vatSubscription)

  private lazy val exitSurveyBase: String = getString(Keys.exitSurveyHost) + getString(Keys.exitSurveyPath)
  override lazy val exitSurveyUrl: String = s"$exitSurveyBase/$contactFormServiceIdentifier"

  override def feedbackUrl(redirect: String): String = s"$contactHost/contact/beta-feedback?service=$contactFormServiceIdentifier" +
    s"&backUrl=${ContinueUrl(vatOptOutServiceUrl + redirect).encodedUrl}"

  override lazy val agentServicesGovUkGuidance: String = getString(Keys.govUkSetupAgentServices)

  override lazy val timeoutPeriod: Int = getInt(Keys.timeoutPeriod)
  override lazy val timeoutCountdown: Int = getInt(Keys.timeoutCountdown)
  override def routeToSwitchLanguage: String => Call = (lang: String) => controllers.routes.LanguageController.switchToLanguage(lang)
  override def languageMap: Map[String, Lang] = Map(
    "english" -> Lang("en"),
    "cymraeg" -> Lang("cy")
  )

  override val agentInvitationsFastTrack: String =
    getString(Keys.agentInvitationsFastTrackUrl) + getString(Keys.agentInvitationsFastTrackPath)

  override val govUkManageClientsDetails: String = getString(Keys.govUkManageClientsDetails)
  override val govUkContactUs: String = getString(Keys.govUkContactUs)
  override val thresholdAmount: String = getString(Keys.thresholdAmount)

  private val vatSummaryServiceUrl: String = getString(Keys.vatSummaryServiceUrl)
  override val vatSummaryServicePath: String = vatSummaryServiceUrl + getString(Keys.vatSummaryServicePath)
}