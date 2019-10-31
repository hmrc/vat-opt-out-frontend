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

import config.{ConfigKeys => Keys}
import javax.inject.{Inject, Singleton}
import play.api.Configuration
import play.api.i18n.Lang
import play.api.mvc.Call
import uk.gov.hmrc.play.binders.ContinueUrl
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

trait AppConfig {
  val contactHost: String
  val assetsPrefix: String
  val reportAProblemPartialUrl: String
  val reportAProblemNonJSUrl: String
  val vatOptOutServicePath: String
  val agentClientLookupHandoff: String
  val agentClientLookupChoicesPath: String
  val signInUrl: String
  def signOutUrl(identifier: String): String
  val unauthorisedSignOutUrl: String
  val manageVatSubscriptionServiceUrl: String
  val manageVatSubscriptionServicePath: String
  val thresholdPreviousYearsUrl: String
  val vatSubscriptionHost: String
  def feedbackUrl(redirect: String): String
  def exitSurveyUrl(identifier: String): String
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
  val accessibilityLinkUrl: String
}

@Singleton
class FrontendAppConfig @Inject()(configuration: Configuration, sc: ServicesConfig) extends AppConfig {

  lazy val contactHost: String = sc.getString(s"contact-frontend.url")
  lazy val contactFormServiceIdentifier = "VATC"

  lazy val assetsPrefix: String = sc.getString(s"assets.url") + sc.getString(s"assets.version")

  lazy val reportAProblemPartialUrl = s"$contactHost/contact/problem_reports_ajax?service=$contactFormServiceIdentifier"
  lazy val reportAProblemNonJSUrl = s"$contactHost/contact/problem_reports_nonjs?service=$contactFormServiceIdentifier"

  private lazy val governmentGatewayHost: String = sc.getString(Keys.governmentGatewayHost)

  private lazy val signInContinueUrl: String = ContinueUrl(vatOptOutServicePath).encodedUrl
  private lazy val signInBaseUrl: String = sc.getString(Keys.signInBaseUrl)
  private lazy val signInOrigin = sc.getString("appName")
  override lazy val signInUrl: String = s"$signInBaseUrl?continue=$signInContinueUrl&origin=$signInOrigin"

  override def signOutUrl(identifier: String): String = s"$governmentGatewayHost/gg/sign-out?continue=${exitSurveyUrl(identifier)}"
  override lazy val unauthorisedSignOutUrl: String = s"$governmentGatewayHost/gg/sign-out?continue=$signInContinueUrl"

  private lazy val vatOptOutServiceUrl: String = sc.getString(Keys.vatOptOutServiceUrl)
  override lazy val vatOptOutServicePath: String =
    vatOptOutServiceUrl + sc.getString(Keys.vatOptOutServicePath)

  private lazy val agentClientLookupServiceUrl: String = sc.getString(Keys.agentClientLookupUrl)
  private lazy val agentClientLookupServicePath: String =
    agentClientLookupServiceUrl + sc.getString(Keys.agentClientLookupPath)
  override lazy val agentClientLookupHandoff: String =
    agentClientLookupServicePath + s"/client-vat-number?redirectUrl=$signInContinueUrl"
  override lazy val agentClientLookupChoicesPath: String =
    agentClientLookupServicePath + sc.getString(Keys.agentClientLookupChoices)

  override lazy val manageVatSubscriptionServiceUrl: String = sc.getString(Keys.manageVatSubscriptionServiceUrl)
  override lazy val manageVatSubscriptionServicePath: String =
    manageVatSubscriptionServiceUrl + sc.getString(Keys.manageVatSubscriptionServicePath)

  override val thresholdPreviousYearsUrl: String = sc.getString(Keys.thresholdPreviousYearsUrl)
  override val vatSubscriptionHost: String = sc.baseUrl(Keys.vatSubscription)

  private lazy val exitSurveyBase: String = sc.getString(Keys.exitSurveyHost) + sc.getString(Keys.exitSurveyPath)
  override def exitSurveyUrl(identifier: String): String = s"$exitSurveyBase/$identifier"

  override def feedbackUrl(redirect: String): String = s"$contactHost/contact/beta-feedback?service=$contactFormServiceIdentifier" +
    s"&backUrl=${ContinueUrl(vatOptOutServiceUrl + redirect).encodedUrl}"

  override lazy val agentServicesGovUkGuidance: String = sc.getString(Keys.govUkSetupAgentServices)

  override lazy val timeoutPeriod: Int = sc.getInt(Keys.timeoutPeriod)
  override lazy val timeoutCountdown: Int = sc.getInt(Keys.timeoutCountdown)
  override def routeToSwitchLanguage: String => Call = (lang: String) => controllers.routes.LanguageController.switchToLanguage(lang)
  override def languageMap: Map[String, Lang] = Map(
    "english" -> Lang("en"),
    "cymraeg" -> Lang("cy")
  )

  override val agentInvitationsFastTrack: String =
    sc.getString(Keys.agentInvitationsFastTrackUrl) + sc.getString(Keys.agentInvitationsFastTrackPath)

  override val govUkManageClientsDetails: String = sc.getString(Keys.govUkManageClientsDetails)
  override val govUkContactUs: String = sc.getString(Keys.govUkContactUs)
  override val thresholdAmount: String = sc.getString(Keys.thresholdAmount)

  private val vatSummaryServiceUrl: String = sc.getString(Keys.vatSummaryServiceUrl)
  override val vatSummaryServicePath: String = vatSummaryServiceUrl + sc.getString(Keys.vatSummaryServicePath)

  override val accessibilityLinkUrl: String = sc.getString(ConfigKeys.vatSummaryServiceUrl) + sc.getString(ConfigKeys.vatSummaryAccessibilityUrl)
}
